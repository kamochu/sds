<?php

namespace Ssg\Model;

use \Ssg\Core\DatabaseFactory;
use Ssg\Core\Model;
use Ssg\Core\Session;
use Ssg\Core\Config;
use Psr\Log\LoggerInterface;

/**
 * MessageModel - offers functions to manage messages in the system (sql, export, ext)
 *
 */
class TaskModel extends Model {

    /**
     * Construct this object by extending the basic Model class
     */
    public function __construct(LoggerInterface $logger = null) {
        parent::__construct($logger);
    }

    /**
     * getTasks - get tasks
     * 
     * @return array containing query result and task data
     */
    public function getTasks($start_index = 0, $limit = 10, $start_date = '', $end_date = '', $batch_id = '', $status = '', $initiator = '', $order = 'DESC') {
        $sql = 'SELECT * FROM tbl_matching_jobs WHERE 1 ';
        $parameters = array();
        //include start_date filter
        if (isset($start_date) && !empty($start_date)) {
            $sql = $sql . " AND date(created_on)>=:start_date";
            $parameters[':start_date'] = $start_date;
        }
        //include end_date filter
        if (isset($end_date) && !empty($end_date)) {
            $sql = $sql . " AND date(created_on)<=:end_date";
            $parameters[':end_date'] = $end_date;
        }
        //include batch_id filter
        if (isset($batch_id) && !empty($batch_id)) {
            $sql = $sql . " AND batch_id=:batch_id";
            $parameters[':batch_id'] = $batch_id;
        }
        //include status filter
        if (isset($status) && !empty($status)) {
            $sql = $sql . " AND status=:status";
            $parameters[':status'] = $status;
        }
        //include initiator filter
        if (isset($initiator) && !empty($initiator)) {
            $sql = $sql . " AND initiator=:initiator";
            $parameters[':initiator'] = $initiator;
        }
        $query_total = $sql; // copy query to be used to get the total number of reords (without the group by and limit clause)
        $sql = $sql . ' ORDER BY id ' . $order . ' LIMIT ' . $start_index . ', ' . $limit;


        // add some logic to handle exceptions in this script
        $row_count = 0;
        $total_records = 0;
        $tasks = '';
        $database = null;
        try {
            $database = DatabaseFactory::getFactory()->getConnection();
        } catch (Exception $ex) {
            $this->logger->info(
                    '{class_mame}|{method_name}|PDOException|{error}|{query}|bind_parameters:{bind_params}', array(
                'class_mame' => __CLASS__,
                'method_name' => __FUNCTION__,
                'error' => $e->getMessage()
                    )
            );
            return array('result' => 3, 'resultDesc' => 'Cannot connect to the database. Error: ' . $ex->getMessage());
        }

        try {
            //get total records for pagination
            $query = $database->prepare($query_total);
            if ($query->execute($parameters)) {
                $total_records = $query->rowCount();
            } else {
                $this->logger->error(
                        '{class_mame}|{method_name}|error executing the query|{error}|{query}|bind_parameters:{bind_params}', array(
                    'class_mame' => __CLASS__,
                    'method_name' => __FUNCTION__,
                    'error' => $database->errorCode(),
                    'query' => $sql,
                    'bind_params' => json_encode($parameters)
                        )
                );
                return array('result' => 5, 'resultDesc' => 'Error executing a query.');
            }

            //get records
            $query = $database->prepare($sql);
            $this->logger->info(
                    '{class_mame}|{method_name}|query and data|{error}|query:{query}|bind_params:{bind_params}', array(
                'class_mame' => __CLASS__,
                'method_name' => __FUNCTION__,
                'error' => $database->errorCode(),
                'query' => $sql,
                'bind_params' => json_encode($parameters)
                    )
            );

            if ($query->execute($parameters)) {
                // fetchAll() is the PDO method that gets all result rows
                $tasks = $query->fetchAll();
                $row_count = $query->rowCount();

                if ($row_count >= 0) {
                    return array('result' => 0, 'resultDesc' => 'Records retrieved successfully.', '_recordsRetrieved' => $row_count, '_totalRecords' => $total_records, 'tasks' => $tasks);
                }
            } else {
                $this->logger->error(
                        '{class_mame}|{method_name}|error executing the query|{error}|query:{query}|bind_params:{bind_params}', array(
                    'class_mame' => __CLASS__,
                    'method_name' => __FUNCTION__,
                    'error' => $database->errorCode(),
                    'query' => $sql,
                    'bind_params' => json_encode($parameters)
                        )
                );
                return array('result' => 5, 'resultDesc' => 'Error executing a query.');
            }
        } catch (PDOException $e) {
            $this->logger->error(
                    '{class_mame}|{method_name}|PDOException|{error}|{query}', array(
                'class_mame' => __CLASS__,
                'method_name' => __FUNCTION__,
                'error' => $e->getMessage(),
                'query' => $sql
                    )
            );
            return array('result' => 4, 'resultDesc' => 'Error executing a query. Error: ' . $e->getMessage());
        }

        return array('result' => 1, 'resultDesc' => 'No records found', 'tasks' => $tasks);
    }

    public function submitTask($reason) {
        //to be used in submission of the task
        $user_id = Session::get("user_id");

        //command to use 
        $command = Config::get('MATCH_JOB_SCRIPT') . " " . $user_id . " '" . $reason . "'";

        $return_var = -1;
        $return_str = "reason cannot be null";
        if (isset($reason) && !empty($reason)) {
            $return_str = system($command, $return_var);
        }
        $this->logger->info('{class_mame}|{method_name}|{reason}|{return_str}', array('class_mame' => __CLASS__, 'method_name' => __FUNCTION__, 'reason' => $reason, 'return_str' => $return_str));
        return array('result' => $return_var, 'resultDesc' => $return_str); // reason cannot be null
    }

}
