<?php

namespace Ssg\Controller;

use Ssg\Core\Controller;
use Ssg\Core\Config;
use Ssg\Core\Session;
use Ssg\Core\Pagination;
use Ssg\Model\TipModel;
use Ssg\Core\Request;
use Ssg\Core\Auth;
use Psr\Log\LoggerInterface;
use \stdClass;

class TipController extends Controller {

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
        $text = Request::get('text');
        $status = Request::get('status');
        $page = (null !== Request::get('page')) ? ((int) Request::get('page')) : 1; //page - default is 1
        $rpp = (int) Config::get('RECORDS_PER_PAGE'); //records per page
        $start_record = ( (int) ( ($page - 1) * $rpp) ); // start record 
        //reset the statuses to default -1
        if (!isset($status) || empty($status))
            $status = -1;

        //request data to be used in calling the model
        $data = array(
            'text' => $text,
            'status' => $status,
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
        $model = new TipModel($this->logger);
        //$start_index=0, $limit=10, $msisdn='', $name='', $location='', $reg_status='-1', $status='-1', $sdp_status='-1', $order='DESC'
        $result = $model->getTips($start_record, $rpp, $text, $status);

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
        if ($status == '-1')
            $data['status'] = '';

        $this->View->render('tip/index', $data);

        //log the event
        $this->logger->info(
                '{class_mame}|{method_name}|result|result:{result}', array(
            'class_mame' => __CLASS__,
            'method_name' => __FUNCTION__,
            'result' => json_encode($result)
                )
        );
        /*
          //get all tips
          $model = new TipModel($this->logger);
          $result = $model->getTips(0,100,'out');
          print_r($result);

          $data=array();
          $this->View->render('tip/index');
         */
    }

    public function view($tip_id) {
        $data = array('tip_id' => $tip_id);
        //log the event
        $this->logger->debug(
                '{class_mame}|{method_name}|{tip_id}|request-data', array(
            'class_mame' => __CLASS__,
            'method_name' => __FUNCTION__,
            'tip_id' => $tip_id
                )
        );

        $tip_model = new TipModel($this->logger);
        $result = $tip_model->getTip($tip_id);
        $data['result'] = $result;

        //success
        if ($result['result'] == 0) {
            $this->View->render('tip/view', $data);
        } else {
            $this->View->render('error/loaderror', $result['resultDesc']);
        }

        //log the event
        $this->logger->info(
                '{class_mame}|{method_name}|{tip_id}|result|result:{result}', array(
            'class_mame' => __CLASS__,
            'method_name' => __FUNCTION__,
            'tip_id' => $tip_id,
            'result' => json_encode($result)
                )
        );
    }

    public function add() {
        //get request data
        $text = Request::post('text', true);
        $start_date = Request::post('start_date', true);
        $end_date = Request::post('end_date', true);

        $data = array(
            'text' => $text,
            'start_date' => $start_date,
            'end_date' => $end_date
        );

        //log the event
        $this->logger->debug(
                '{class_mame}|{method_name}|request-data|{data}', array(
            'class_mame' => __CLASS__,
            'method_name' => __FUNCTION__,
            'data' => json_encode($data)
                )
        );

        if (null !== Request::post('action', true)) {
            //form submitted, processing to happen below
            $tip_model = new TipModel($this->logger);
            $result = $tip_model->addTip($data);
            $data['result'] = $result;

            //success
            if ($result['result'] == 0) {
                Session::add('feedback_positive', 'Tip created successfully');
            } else {
                Session::add('feedback_negative', 'Tip creation failed. Error: ' . $result['result'] . ' - ' . $result['resultDesc']);
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
        $this->View->render('tip/add', $data);
    }

    public function edit($tip_id) {
        //get request data
        $id = Request::post('id', true);
        $text = Request::post('text', true);
        $start_date = Request::post('start_date', true);
        $end_date = Request::post('end_date', true);

        $data = array(
            'id' => $id,
            'text' => $text,
            'start_date' => $start_date,
            'end_date' => $end_date
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
            $model = new TipModel($this->logger);

            //create tip object
            $tip = new stdClass();
            $tip->id = $id;
            $tip->tip = $text;
            $tip->effective_date = $start_date;
            $tip->expiry_date = $end_date;

            $result = $model->updateTip($tip);
            $data['result'] = $result;
            //success
            if ($result['result'] == 0) {
                Session::add('feedback_positive', 'Tip updated successfully');
            } else {
                Session::add('feedback_negative', 'Tip updating failed. Error: ' . $result['result'] . ' - ' . $result['resultDesc']);
            }

            //log the event
            $this->logger->info(
                    '{class_mame}|{method_name}|edit-tip-result|{result}', array(
                'class_mame' => __CLASS__,
                'method_name' => __FUNCTION__,
                'result' => json_encode($result)
                    )
            );
        } else {
            //load servive data from windows
            $model = new TipModel($this->logger);
            $result = $model->getTip($tip_id);
            $data['result'] = $result;
            $tip_data = json_decode(json_encode($result['tip']), true);

            $tip_data['text'] = $tip_data['tip'];
            $tip_data['start_date'] = $tip_data['effective_date'];
            $tip_data['end_date'] = $tip_data['expiry_date'];
            //successful loading of tip
            if ($result['result'] == 0) {
                $data = $tip_data;
            } else {
                Session::add('feedback_negative', 'Tip ' . $id . ' loading failed. Error: ' . $result['result'] . ' - ' . $result['resultDesc']);
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
        $this->View->render('tip/edit', $data);
    }

    public function enable($tip_id) {
        //log the event
        $this->logger->debug(
                '{class_mame}|{method_name}|{tip_id}|enable-tip-request', array(
            'class_mame' => __CLASS__,
            'method_name' => __FUNCTION__,
            'tip_id' => $tip_id
                )
        );

        $model = new TipModel($this->logger);
        $result = $model->getTip($tip_id);
        //success
        if ($result['result'] != 0) {
            Session::add('feedback_negative', 'Tip updating failed. Error: ' . $result['result'] . ' - ' . $result['resultDesc']);
        } else {
            $result = $model->enable($result['tip']);

            //success
            if ($result['result'] == 0) {
                Session::add('feedback_positive', 'Tip enabled successfully');
            } else {
                Session::add('feedback_negative', 'Tip updating failed. Error: ' . $result['result'] . ' - ' . $result['resultDesc']);
            }
        }

        $data = array('tip_id' => $tip_id, 'result' => $result);
        $this->View->render('tip/enable', $data);

        //log the event
        $this->logger->info(
                '{class_mame}|{method_name}|{tip_id}|result|{result}', array(
            'class_mame' => __CLASS__,
            'method_name' => __FUNCTION__,
            'tip_id' => $tip_id,
            'result' => json_encode($result)
                )
        );
    }

    public function disable($tip_id) {
        //log the event
        $this->logger->debug(
                '{class_mame}|{method_name}|{tip_id}|enable-tip-request', array(
            'class_mame' => __CLASS__,
            'method_name' => __FUNCTION__,
            'tip_id' => $tip_id
                )
        );

        $model = new TipModel($this->logger);
        $result = $model->getTip($tip_id);
        //success
        if ($result['result'] != 0) {
            Session::add('feedback_negative', 'Tip updating failed. Error: ' . $result['result'] . ' - ' . $result['resultDesc']);
        } else {
            $result = $model->disable($result['tip']);

            //success
            if ($result['result'] == 0) {
                Session::add('feedback_positive', 'Tip disabled successfully');
            } else {
                Session::add('feedback_negative', 'Tip updating failed. Error: ' . $result['result'] . ' - ' . $result['resultDesc']);
            }

            $data = array('tip_id' => $tip_id, 'result' => $result);
        }
        $this->View->render('tip/disable', $data);

        //log the event
        $this->logger->info(
                '{class_mame}|{method_name}|{tip_id}|result|{result}', array(
            'class_mame' => __CLASS__,
            'method_name' => __FUNCTION__,
            'tip_id' => $tip_id,
            'result' => json_encode($result)
                )
        );
    }

    public function delete($tip_id) {
        //get request data
        $id = $tip_id;

        $data = array(
            'id' => $id,
        );

        //log the event
        $this->logger->debug(
                '{class_mame}|{method_name}|request-data', array(
            'class_mame' => __CLASS__,
            'method_name' => __FUNCTION__,
            'request-data' => json_encode($data)
                )
        );

        if (null !== Request::post('action', true)) {
            //form submitted, processing to happen below
            $model = new TipModel($this->logger);
            $result = $model->deleteTip($tip_id);
            $data['result'] = $result;
            //success
            if ($result['result'] == 0) {
                Session::add('feedback_positive', 'Tip deleted successfully');
            } else {
                Session::add('feedback_negative', 'Tip deletion failed. Error: ' . $result['result'] . ' - ' . $result['resultDesc']);
            }

            //log the event
            $this->logger->debug(
                    '{class_mame}|{method_name}|result|{result}', array(
                'class_mame' => __CLASS__,
                'method_name' => __FUNCTION__,
                'result' => $result['result'],
                'result_desc' => json_encode($result)
                    )
            );
        } else {
            //load servive data from windows
            $model = new TipModel($this->logger);
            $result = $model->getTip($tip_id);

            $data['result'] = $result;

            //successful loading of tip
            if ($result['result'] == 0) {
                $data['result'] = $result;
            } else {
                Session::add('feedback_negative', 'Tip ' . $tip_id . ' loading failed. Error: ' . $result['result'] . ' - ' . $result['resultDesc']);
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

        $this->View->render('tip/delete', $data);
    }

}
