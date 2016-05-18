<?php

namespace Ssg\Controller;

use Ssg\Core\Controller;
use Ssg\Core\Auth;
use Psr\Log\LoggerInterface;

class ReportController extends Controller {

    /**
     * Construct this object by extending the basic Controller class
     */
    public function __construct(LoggerInterface $logger = null) {
        parent::__construct($logger);

        // this entire controller should only be visible/usable by logged in users, so we put authentication-check here
        Auth::checkAuthentication();

        //check the IP whitelist
        Auth::checkIPAuthentication();
    }

    /**
     * Handles what happens when user moves to URL/delivery
     */
    public function index() {
        //bad request error
        $data = array();
        $this->View->render('report/index', $data);
    }

}
