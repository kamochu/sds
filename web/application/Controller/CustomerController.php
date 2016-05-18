<?php

namespace Ssg\Controller;

use Ssg\Core\Controller;
use Ssg\Core\Config;
use Ssg\Core\Session;
use Ssg\Core\Pagination;
use Ssg\Model\CustomerModel;
use Ssg\Core\Request;
use Ssg\Core\Auth;
use Psr\Log\LoggerInterface;

class CustomerController extends Controller {

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

        /*
          Initialize the request data
         */
        //get request data
        $msisdn = Request::get('msisdn');
        $name = Request::get('name');
        $location = Request::get('location');
        $reg_status = Request::get('reg_status');
        $status = Request::get('status');
        $sdp_status = Request::get('sdp_status');
        $page = (null !== Request::get('page')) ? ((int) Request::get('page')) : 1; //page - default is 1
        $rpp = (int) Config::get('RECORDS_PER_PAGE'); //records per page
        $start_record = ( (int) ( ($page - 1) * $rpp) ); // start record 
        //reset the statuses to default -1
        if (!isset($reg_status) || empty($reg_status)) {
            $reg_status = -1;
        }
        if (!isset($status) || empty($status)) {
            $status = -1;
        }
        if (!isset($sdp_status) || empty($sdp_status)) {
            $sdp_status = -1;
        }

        //request data to be used in calling the model
        $data = array(
            'msisdn' => $msisdn,
            'name' => $name,
            'location' => $location,
            'reg_status' => $reg_status,
            'reg_status' => $reg_status,
            'status' => $status,
            'sdp_status' => $sdp_status,
            'rpp' => $rpp,
            'start_record' => $start_record,
        );

        //log the event
        $this->logger->debug(
                '{class_mame}|{method_name}|request|request-data:{data}', array(
            'class_mame' => __CLASS__,
            'method_name' => __FUNCTION__,
            'data' => json_encode($data)
                )
        );

        //call the model 
        $model = new CustomerModel($this->logger);
        //$start_index=0, $limit=10, $msisdn='', $name='', $location='', $reg_status='-1', $status='-1', $sdp_status='-1', $order='DESC'
        $result = $model->getCustomers($start_record, $rpp, $msisdn, $name, $location, $reg_status, $status, $sdp_status);

        //add result
        $data['result'] = $result;


        //add some pagination logic here
        $total_records = isset($result['_totalRecords']) ? $result['_totalRecords'] : 0;
        $pagination = (new Pagination());
        $pagination->setCurrent($page);
        $pagination->setTotal($total_records);
        $markup = $pagination->parse();
        $data['markup'] = $markup;
        //reset the statuses to default -1
        if ($reg_status == '-1') {
            $data['reg_status'] = '';
        }
        if ($status == '-1') {
            $data['status'] = '';
        }
        if ($sdp_status == '-1') {
            $data['sdp_status'] = '';
        }
        $this->View->render('customer/index', $data);

        //log the event
        $this->logger->info(
                '{class_mame}|{method_name}|result|result:{result}', array(
            'class_mame' => __CLASS__,
            'method_name' => __FUNCTION__,
            'result' => json_encode($result)
                )
        );
    }

    public function pdf() {
        //to be implemented
        $this->index();
    }

    public function view($sub_id) {
        $data = array('sub_id' => $sub_id);
        //log the event
        $this->logger->debug(
                '{class_mame}|{method_name}|{sub_id}|request-data', array(
            'class_mame' => __CLASS__,
            'method_name' => __FUNCTION__,
            'sub_id' => $sub_id
                )
        );

        $model = new CustomerModel($this->logger);
        $result = $model->getCustomer($sub_id);
        $data['result'] = $result;

        //success
        if ($result['result'] == 0) {
            
            $msisdn = $result['customer']->msisdn;

            //pagination stuff here
            $page = (null !== Request::get('page')) ? ((int) Request::get('page')) : 1; //page - default is 1
            $rpp = (int) Config::get('RECORDS_PER_PAGE'); //records per page
            $start_record = ( (int) ( ($page - 1) * $rpp) ); // start record 
            //$start_index=0, $limit=10, $order='DESC')
            $result2 = $model->getCustomerActivityLogs($sub_id, $msisdn, $start_record, $rpp);

            //add some more pagination logic here
            $total_records = isset($result2['_totalRecords']) ? $result2['_totalRecords'] : 0;
            $pagination = (new Pagination());
            $pagination->setCurrent($page);
            $pagination->setTotal($total_records);
            $markup = $pagination->parse();
            $data['markup'] = $markup;

            if ($result2['result'] == 0) {
                $data['activities'] = $result2['activities'];
                $data['total_records'] = $total_records;
            }

            $this->View->render('customer/view', $data);
        } else {
            $this->View->render('error/loaderror', $result['resultDesc']);
        }

        //log the event
        $this->logger->info(
                '{class_mame}|{method_name}|{sub_id}|result|result:{result}', array(
            'class_mame' => __CLASS__,
            'method_name' => __FUNCTION__,
            'sub_id' => $sub_id,
            'result' => json_encode($result)
                )
        );
    }

    public function edit($sub_id) {
        //get request data
        $id = Request::post('id', true);
        $msisdn = Request::post('msisdn', true);
        $name = Request::post('name', true);
        $age = Request::post('age', true);
        $sex = Request::post('sex', true);
        $location = Request::post('location', true);
        $status = Request::post('status', true);
        $status_reason = Request::post('status_reason', true);
        $reg_status = Request::post('reg_status', true);

        $data = array(
            'id' => $id,
            'msisdn' => $msisdn,
            'name' => $name,
            'age' => $age,
            'sex' => $sex,
            'location' => $location,
            'status' => $status,
            'status_reason' => $status_reason,
            'reg_status' => $reg_status
        );

        //log the event
        $this->logger->debug(
                '{class_mame}|{method_name}|request-data|{data}', array(
            'class_mame' => __CLASS__,
            'method_name' => __FUNCTION__,
            'request-data' => json_encode($data)
                )
        );

        if (null !== Request::post('action', true)) {
            //form submitted, processing to happen below
            $model = new CustomerModel($this->logger);

            //create customer object
            $customer = json_decode(json_encode($data), false);
            $customer->msisdn = $data['msisdn']; //issue with encoding tel:XXXXXXX

            $result = $model->updateCustomer($customer);
            $data['result'] = $result;

            //success
            if ($result['result'] == 0) {
                Session::add('feedback_positive', 'Customer updated successfully');
            } else {
                Session::add('feedback_negative', 'Customer updating failed. Error: ' . $result['result'] . ' - ' . $result['resultDesc']);
            }

            //log the event
            $this->logger->info(
                    '{class_mame}|{method_name}|result|{result}', array(
                'class_mame' => __CLASS__,
                'method_name' => __FUNCTION__,
                'result' => json_encode($result)
                    )
            );
        } else {
            //load servive data from windows
            $model = new CustomerModel($this->logger);

            $result = $model->getCustomer($sub_id);
            $data['result'] = $result;

            //successful loading of tip
            if ($result['result'] == 0) {
                $data = $result['customer'];
            } else {
                Session::add('feedback_negative', 'Customer ' . $id . ' loading failed. Error: ' . $result['result'] . ' - ' . $result['resultDesc']);
            }
            //log the event
            $this->logger->debug(
                    '{class_mame}|{method_name}|result|{result}', array(
                'class_mame' => __CLASS__,
                'method_name' => __FUNCTION__,
                'result' => json_encode($result)
                    )
            );
        }
        $this->View->render('customer/edit', $data);
    }

}
