<?php

namespace Ssg\Core;

/**
 * Class Application
 * The heart of the application
 */
class Application {

    /** @var mixed Instance of the controller */
    private $controller;

    /** @var array URL parameters, will be passed to used controller-method */
    private $parameters = array();

    /** @var string Just the name of the controller, useful for checks inside the view ("where am I ?") */
    private $controller_name;

    /** @var string Just the name of the controller's method, useful for checks inside the view ("where am I ?") */
    private $action_name;

    /** @var string Just the name of the logger to be created */
    private $logger_name;

    /**
     * Start the application, analyze URL elements, call according controller/method or relocate to fallback location
     */
    public function __construct() {
        // create array with URL parts in $url
        $this->splitUrl();

        // creates controller and action names (from URL input)
        $this->createControllerAndActionNames();

        //instantiate a logger to be used in the constructor
        $logger = new PardusLogger($this->logger_name);

        // instantiate the constructor object
        if (class_exists($this->controller_name)) {
            $this->controller = new $this->controller_name($logger);

            // check for method: does such a method exist in the controller ?
            if (method_exists($this->controller, $this->action_name)) {
                if (!empty($this->parameters)) {
                    // call the method and pass arguments to it
                    call_user_func_array(array($this->controller, $this->action_name), $this->parameters);
                } else {
                    // if no parameters are given, just call the method without parameters, like $this->index->index();
                    $this->controller->{$this->action_name}();
                }
            } else {
                header('location: ' . Config::get('URL') . 'error');
            }
        } else {
            header('location: ' . Config::get('URL') . 'error');
        }
    }

    /**
     * Get and split the URL
     */
    private function splitUrl() {
        if (Request::get('url')) {

            // split URL
            $url = trim(Request::get('url'), '/');
            $url = filter_var($url, FILTER_SANITIZE_URL);
            $url = explode('/', $url);

            // put URL parts into according properties
            $this->controller_name = isset($url[0]) ? $url[0] : null;
            $this->action_name = isset($url[1]) ? $url[1] : null;

            // remove controller name and action name from the split URL
            unset($url[0], $url[1]);

            // rebase array keys and store the URL parameters
            $this->parameters = array_values($url);
        }
    }

    /**
     * Checks if controller and action names are given. If not, default values are put into the properties.
     * Also renames controller to usable name.
     */
    private function createControllerAndActionNames() {
        // check for controller: no controller given ? then make controller = default controller (from config)
        if (!$this->controller_name) {
            $this->controller_name = Config::get('DEFAULT_CONTROLLER');
        }

        //initialize the logger name (copy the name of the controller before the controller namespaece and  prefix are added)
        $this->logger_name = $this->controller_name;

        //modify the controller to include the namespace
        $this->controller_name = '\Ssg\Controller\\' . ucwords($this->controller_name);


        // check for action: no action given ? then make action = default action (from config)
        if (!$this->action_name OR ( strlen($this->action_name) == 0)) {
            $this->action_name = Config::get('DEFAULT_ACTION');
        }

        // rename controller name to real controller class/file name ("index" to "IndexController")
        $this->controller_name = $this->controller_name . 'Controller';
    }

}