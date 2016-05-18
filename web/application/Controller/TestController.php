<?php

namespace Ssg\Controller;

use Ssg\Core\Controller;
use Psr\Log\LoggerInterface;
use Ssg\Core\Auth;

/**
 * Description of TestController
 *
 * @author kamochu
 */
class TestController extends Controller {

    /**
     * Construct this object by extending the basic Controller clas
     */
    public function __construct(LoggerInterface $logger = null) {
        parent::__construct($logger);

        // this entire controller should only be visible/usable by logged in users, so we put authentication-check here
        Auth::checkAuthentication();

        //check the IP whitelist
        Auth::checkIPAuthentication();
    }

    public function index() {
        $this->test();
    }

    public function test() {
        echo "This is a test";
    }

}
