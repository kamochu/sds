<?php

namespace Ssg\Controller;

use Psr\Log\LoggerInterface;
use Ssg\Core\Auth;
use Ssg\Core\Config;
use Ssg\Core\Controller;
use Ssg\Core\Pagination;
use Ssg\Core\Request;
use Ssg\Core\Session;
use Ssg\Model\TaskModel;

class TaskController extends Controller {

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

    /**
     * Handles what happens when user moves to URL/delivery
     */
    public function index() {
        /*
          Initialize the request data
         */
        //get request data
        $start_date = Request::get('start_date');
        $end_date = Request::get('end_date');
        $batch_id = Request::get('batch_id');
        $status = Request::get('status');
        $initiator = Request::get('initiator');

        $page = (null !== Request::get('page')) ? ((int) Request::get('page')) : 1; //page - default is 1
        $rpp = (int) Config::get('RECORDS_PER_PAGE'); //records per page
        $start_record = ( (int) ( ($page - 1) * $rpp) ); // start record 
        //set default start date - 1 month ago
        if (!isset($start_date) || $start_date == '') {
            $date = date_create(date('Y-m-d'));
            date_sub($date, date_interval_create_from_date_string('1 months'));
            $start_date = date_format($date, 'Y-m-d');
        }

        //set default end date  - current day
        if (!isset($end_date) || $end_date == '') {
            $end_date = date('Y-m-d');
        }

        //request data to be used in calling the model
        $data = array(
            'start_date' => $start_date,
            'end_date' => $end_date,
            'batch_id' => $batch_id,
            'status' => $status,
            'initiator' => $initiator,
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
        $model = new TaskModel($this->logger);
        $result = $model->getTasks($start_record, $rpp, $start_date, $end_date, $batch_id, $status, $initiator);

        //add result
        $data['result'] = $result;


        //add some pagination logic here
        $total_records = isset($result['_totalRecords']) ? $result['_totalRecords'] : 0;
        $pagination = (new Pagination());
        $pagination->setCurrent($page);
        $pagination->setTotal($total_records);
        $markup = $pagination->parse();
        $data['markup'] = $markup;

        $this->View->render('task/index', $data);

        //log the event
        $this->logger->info(
                '{class_mame}|{method_name}|result|result:{result}', array(
            'class_mame' => __CLASS__,
            'method_name' => __FUNCTION__,
            'result' => json_encode($result)
                )
        );
    }

    public function submit() {
        //get request data
        $reason = Request::post('reason', true);

        $data = array(
            'reason' => $reason,
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
            $model = new TaskModel($this->logger);
            $result = $model->submitTask($reason);

            $data['result'] = $result;
            //success
            if ($result['result'] == 0) {
                Session::add('feedback_positive', 'Task subsmitted successfully');
            } else {
                Session::add('feedback_negative', 'Task submission failed. Error: ' . $result['result'] . ' - ' . $result['resultDesc']);
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
        }

        $this->View->render('task/submit', $data);
    }

}
