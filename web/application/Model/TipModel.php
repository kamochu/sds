<?php
namespace Ssg\Model;

use \Ssg\Core\Config;
use \Ssg\Core\SDP;
use \Ssg\Core\Model;
use \Ssg\Core\Session;
use \Ssg\Core\DatabaseFactory;
use \Psr\Log\LoggerInterface;
use \Psr\Log\NullLogger;
use \Exception;
use \stdClass;

/**
 * TipModel - offers utility functions to manage tips
 *
 */
class TipModel extends Model
{
	/**
     * Construct this object by extending the basic Model class
     */
    public function __construct(LoggerInterface $logger = null)
    {
        parent::__construct($logger);
    }
	
	/**
     * Enable Tip
     *
     * @param string $tip_id the tip id
	 * @return array containing query result and tip data
     */
	public function enable($tip)
	{
		$tip->status = 1; //enable
		return $this->saveTipStatus($tip);
	}
	
	/**
     * Disable tip
     *
     * @param string $tip_id tip id
	 * @return bool TRUE if enable is successful, FALSE if enable fails
     */
	public function disable($tip)
	{
		$tip->status = 0; //disable
		return $this->saveTipStatus($tip);
	}
	
	
	
	/**
     * Get tip date
     *
     * @param string $tip_id tip id
	 * @return array containing query result and tip data
     */
	public function getTip($tip_id)
	{	
		//get the database connection
		$database=null;
		try {
			$database = DatabaseFactory::getFactory()->getConnection();
		} catch (Exception $ex) {
			return  array('result' => 3, 'resultDesc' => 'Cannot connect to the database. Error: '.$ex->getMessage(), 'tip' => new stdClass()); 
		}
		
		//prepare and execute the query
		try {		
			$sql = "SELECT * FROM tbl_tips WHERE id = :tip_id LIMIT 1";
			$query = $database->prepare($sql);
			$bind_parameters = array(':tip_id' => $tip_id);
			
			if ($query->execute($bind_parameters)) {
				$tip = $query->fetch();
				if ($query->rowCount() < 1) {	
				   return array('result' => 1, 'resultDesc' => 'Tip with id '.$tip_id.' not found.', 'tip' => new stdClass()); 
				}else{
					return array('result' => 0, 'resultDesc' => 'Tip found.', 'tip' => $tip); 
				}
			} else {	
				$this->logger->error(
					'{class_mame}|{method_name}|{tip_id}|error executing the query|{error}|{query}|bind_parameters:{bind_params}',
					array(
						'class_mame'=>__CLASS__,
						'method_name'=>__FUNCTION__,
						'tip_id'=>$tip_id,
						'error'=>$database->errorCode(),
						'query'=>$sql,
						'bind_params'=>json_encode($bind_parameters)
					)
				);
				return  array('result' => 5, 'resultDesc' => 'Error executing a query.', 'tip' => new stdClass()); 
			}
		} catch (PDOException $e) {
			return  array('result' => 4, 'resultDesc' => 'Error executing a query. Error: '.$e->getMessage(), 'tip' => new stdClass()); 
		}
		
        return array('result' => 7, 'resultDesc' => 'Unknown error', 'tip' => new stdClass()); 
	}
	
	
	/**
     * Update the tip table to indicate the new status and the correlator.
     *
     * @param string $tip tips data
	 * @return array containing query result and tip data
     */
	public function saveTipStatus($tip)
	{
		//get the parameters to be used in saving 
		$tip_id = $tip->id;
		$status = $tip->status;
		
		//initialize the database connection
		$database=null;
		$errorCode='';
		try {
			$database = DatabaseFactory::getFactory()->getConnection();
		} catch (Exception $ex) {
			$this->logger->error(
				'{class_mame}|{method_name}|cannot connect to database|{exception}',
				array(
					'class_mame'=>__CLASS__,
					'method_name'=>__FUNCTION__,
					'tip_id'=>$tip_id,
					'exception'=>$ex->getMessage()
				)
			);
			return  array('result' => 3, 'resultDesc' => 'Cannot connect to the database. Error: '.$ex->getMessage()); 
		}
		
		//saving the data
		try{
			$database->beginTransaction();
			$sql='UPDATE tbl_tips SET status=:status, last_updated_on = NOW(), last_updated_by=:last_updated_by WHERE id=:tip_id';
			$query = $database->prepare($sql);
			$bind_parameters = array(':tip_id' => $tip_id , ':last_updated_by' => Session::get('user_id'), ':status' => $status);
			$this->logger->info("query: $sql, bind paramters: ".implode(',',$bind_parameters));
			
			//execute the query and check the status
			if ($query->execute($bind_parameters)) {
				$row_count = $query->rowCount();
				$errorCode = $database->errorCode();
				$database->commit();
				
				if ($row_count == 1) {	
					return array('result' => 0, 'resultDesc' => 'Saving success', 'tip' => $tip); 
				}
				
			}else{
				$this->logger->error(
					'{class_mame}|{method_name}|{tip_id}|error executing the query|{query}|bind_parameters:{bind_params}',
					array(
						'class_mame'=>__CLASS__,
						'method_name'=>__FUNCTION__,
						'tip_id'=>$tip_id,
						'query'=>$sql,
						'bind_params'=>implode(',',$bind_parameters)
					)
				);
				return  array('result' => 5, 'resultDesc' => 'Error executing a query.'); 
			}
		} catch (PDOException $e) {
			return  array('result' => 4, 'resultDesc' => 'Error executing a query. Error: '.$e->getMessage()); 
		}
		//default
		return array('result' => 7, 'resultDesc' => 'Unknown error', 'tip' => $tip); 
	}
	
	
	/**
     * addTip - add tip
     *
     * @param string $data tip data
	 * @return array containing query result and tip data
     */
	public function addTip($data)
	{
		//initialize tip data
		$tip="";
		$effective_date="";
		$expiry_date="";
		$status=0;
		$last_updated_by= Session::get('user_id');
		
		//populate the data with the request data
		if(isset($data['text'])) $tip=$data['text'];
		if(isset($data['start_date'])) $effective_date=$data['start_date'];
		if(isset($data['end_date'])) $expiry_date=$data['end_date'];		
		
		if ( 
			empty($tip) || !isset($tip) || 
			empty($effective_date) || !isset($effective_date) || 
			empty($expiry_date) || !isset($expiry_date)
		) {
			return  array('result' => 10, 'resultDesc' => 'Missing mandatory data.'); 
		}
		
		$database=null;
		try {
			$database = DatabaseFactory::getFactory()->getConnection();
		} catch (Exception $ex) {
			return  array('result' => 3, 'resultDesc' => 'Cannot connect to the database. Error: '.$ex->getMessage()); 
		}
		
		try {		
			$database->beginTransaction();
			$sql='INSERT INTO tbl_tips (tip, effective_date, expiry_date, status, created_on, last_updated_on, last_updated_by) VALUES (:tip, :effective_date, :expiry_date, :status, NOW(), NOW(), :last_updated_by)';
			$query = $database->prepare($sql);
			
			$bind_patameters = array(':tip' => $tip , ':effective_date' => $effective_date, ':expiry_date' => $expiry_date, ':status' => $status, ':last_updated_by' => $last_updated_by);
			
			$this->logger->debug(
				'{class_mame}|{method_name}|error executing the query|{error}|{query}|bind_parameters:{bind_params}',
				array(
					'class_mame'=>__CLASS__,
					'method_name'=>__FUNCTION__,
					'error'=>$database->errorCode(),
					'query'=>$sql,
					'bind_params'=>json_encode($bind_patameters)
				)
			);	
			
			if ($query->execute($bind_patameters)) {
				//add last insert id, may be used in the next method calls
				$last_insert_id = $database->lastInsertId();
				
				$row_count = $query->rowCount();
				$database->commit();
				
				if ($row_count == 1) {	
					return array('result'=>0, 'resultDesc'=>'Saving successful', '_lastInsertID'=>$last_insert_id );
				}
			} else {	
				$this->logger->error(
					'{class_mame}|{method_name}|error executing the query|{error}|{query}|bind_parameters:{bind_params}',
					array(
						'class_mame'=>__CLASS__,
						'method_name'=>__FUNCTION__,
						'error'=>$database->errorCode(),
						'query'=>$sql,
						'bind_params'=>json_encode($bind_patameters)
					)
				);
				return  array('result' => 5, 'resultDesc' => 'Error executing a query.'); 
			}
		} catch (PDOException $e) {
			return  array('result' => 4, 'resultDesc' => 'Error executing a query. Error: '.$e->getMessage()); 
		}
		$this->logger->error(
			'{class_mame}|{method_name}|error executing the query|{error}|{query}|bind_parameters:{bind_params}',
			array(
				'class_mame'=>__CLASS__,
				'method_name'=>__FUNCTION__,
				'error'=>$database->errorCode(),
				'query'=>$sql,
				'bind_params'=>json_encode($bind_patameters)
			)
		);
		
		return array('result'=>1, 'resultDesc'=>'Adding tip record failed - '.$errorCode, 'tip'=>$data);
	} 
	
	
	/**
     * updatetip - updates existing tip data except status 
	 * which are manipulated by enable and disable tip methods
     *
     * @param string $tip tip data
	 * @return array containing query result and tip data
     */
	public function updateTip($tip)
	{	
		//initialize tip data
		$id="";
		$text="";
		$effective_date="";
		$expiry_date="";
		$status=0;
		$last_updated_by= Session::get('user_id');
		
		
		//populate the data with the request data
		if(isset($tip->id)) $id=$tip->id;
		if(isset($tip->tip)) $text=$tip->tip;
		if(isset($tip->effective_date)) $effective_date=$tip->effective_date;
		if(isset($tip->expiry_date)) $expiry_date=$tip->expiry_date;	
		
		//check whether ther tip exists
		$query_result = self::getTip($id);
		
		//query failure
		if($query_result['result'] != 0) {
			return $query_result; // return the query response error 
		}
		
		$database=null;
		try {
			$database = DatabaseFactory::getFactory()->getConnection();
		} catch (Exception $ex) {
			return  array('result' => 3, 'resultDesc' => 'Cannot connect to the database. Error: '.$ex->getMessage()); 
		}
		
		try {		
			$database->beginTransaction();
			$sql='UPDATE tbl_tips SET tip=:tip, effective_date=:effective_date, expiry_date = :expiry_date, last_updated_on=NOW(), last_updated_by = :last_updated_by WHERE id=:id';
			$query = $database->prepare($sql);
		
			$bind_patameters = array(':id' => $id, 
									':tip' => $text , 
									':effective_date' => $effective_date, 
									':expiry_date' => $expiry_date, 
									':last_updated_by' => $last_updated_by);
			
			$this->logger->debug(
				'{class_mame}|{method_name}|error executing the query|{error}|{query}|bind_parameters:{bind_params}',
				array(
					'class_mame'=>__CLASS__,
					'method_name'=>__FUNCTION__,
					'error'=>$database->errorCode(),
					'query'=>$sql,
					'bind_params'=>json_encode($bind_patameters)
				)
			);	
			
			if ($query->execute($bind_patameters)) {
				$row_count = $query->rowCount();
				$errorCode = $database->errorCode();
				$database->commit();
				
				if ($row_count == 1) {	
					return array('result'=>0, 'resultDesc'=>'Tip updated successfully.', 'tip'=>$tip);
				}
			} else {	
				$this->logger->error(
					'{class_mame}|{method_name}|error executing the query|{error}|{query}|bind_parameters:{bind_params}',
					array(
						'class_mame'=>__CLASS__,
						'method_name'=>__FUNCTION__,
						'error'=>$database->errorCode(),
						'query'=>$sql,
						'bind_params'=>json_encode($bind_patameters)
					)
				);
				return  array('result' => 5, 'resultDesc' => 'Error executing a query.'); 
			}
		} catch (PDOException $e) {
			return  array('result' => 4, 'resultDesc' => 'Error executing a query. Error: '.$e->getMessage()); 
		}
		
		return array('result'=>1, 'resultDesc'=>'Updating records failed - '.$errorCode, 'tip'=>$tip);
	} 
	
	
	/**
     * deleteTip - deletes the tip from the system
	 * Note: Remember to delete the configurations file
     *
     * @param string $id tip data
	 * @return array containing query result and tip data
     */
	public function deleteTip($id)
	{	
		$database=null;
		try {
			$database = DatabaseFactory::getFactory()->getConnection();
		} catch (Exception $ex) {
			return  array('result' => 3, 'resultDesc' => 'Cannot connect to the database. Error: '.$ex->getMessage()); 
		}
		
		try {		
			$database->beginTransaction();
			$sql='DELETE FROM tbl_tips WHERE id = :id LIMIT 1';
			$query = $database->prepare($sql);
			
			$bind_patameters = array(':id' => $id);
			
			if ($query->execute($bind_patameters)) {
				
				$row_count = $query->rowCount();
				$errorCode = $database->errorCode();
				$database->commit();
				
				if ($row_count == 1) {	
					return array('result'=>0, 'resultDesc'=>'Record deleted successsfully', 'tip'=> new stdClass()); ;
				}
			} else {	
				$this->logger->error(
					'{class_mame}|{method_name}|error executing the query|{error}|{query}|bind_parameters:{bind_params}',
					array(
						'class_mame'=>__CLASS__,
						'method_name'=>__FUNCTION__,
						'error'=>$database->errorCode(),
						'query'=>$sql,
						'bind_params'=>json_encode($bind_patameters)
					)
				);
				return  array('result' => 5, 'resultDesc' => 'Error executing a query.'); 
			}
		} catch (PDOException $e) {
			return  array('result' => 4, 'resultDesc' => 'Error executing a query. Error: '.$e->getMessage()); 
		}
		
		return array('result'=>1, 'resultDesc'=>'No record deleted - '.$errorCode, 'tip'=>new stdClass());
	} 
	
	
	/**
     * getTip - deletes the tip from the system
	 * 
	 * @return array containing query result and tip data
     */
	public function getTips($start_index=0, $limit=10, $text='', $status='-1', $order='DESC')
	{
        $sql = 'SELECT id, tip, effective_date, expiry_date, status, created_on, last_updated_on, last_updated_by FROM tbl_tips WHERE 1 ';
		$parameters = array();
		//include status filter
		if (isset($status) && $status!='-1') {
			$sql= $sql." AND status=:status";
			$parameters[':status']=$status;
		}
		
		//include text like filter
		if (isset($text) && !empty($text)) {
			$sql= $sql." AND tip LIKE '%$text%'";
			//$parameters[':text']=$text;
		}
		
		$query_total = $sql; // copy query to be used to get the total number of reords (without the group by and limit clause)
		$sql= $sql.' ORDER BY id '.$order.' LIMIT '.$start_index.', '.$limit;
		
		
		// add some logic to handle exceptions in this script
		$row_count=0; 
		$total_records=0;
		$tips='';
		$database=null;
		try {
			$database = DatabaseFactory::getFactory()->getConnection();
		} catch (Exception $ex) {
			$this->logger->error(
				'{class_mame}|{method_name}|PDOException|{error}|{query}|bind_parameters:{bind_params}',
				array(
					'class_mame'=>__CLASS__,
					'method_name'=>__FUNCTION__,
					'error'=>$e->getMessage()
				)
			);
			return  array('result' => 3, 'resultDesc' => 'Cannot connect to the database. Error: '.$ex->getMessage()); 
		}
		
		try {
			//get total records for pagination
			$query = $database->prepare($query_total);	
			if ($query->execute($parameters)) {
				$total_records = $query->rowCount();
			} else {	
				$this->logger->error(
					'{class_mame}|{method_name}|error executing the query|{error}|{query}|bind_parameters:{bind_params}',
					array(
						'class_mame'=>__CLASS__,
						'method_name'=>__FUNCTION__,
						'error'=>$database->errorCode(),
						'query'=>$sql,
						'bind_params'=>json_encode($parameters)
					)
				);
				return  array('result' => 5, 'resultDesc' => 'Error executing a query.'); 
			}
			
			//get records
			$query = $database->prepare($sql);	
			$this->logger->info(
					'{class_mame}|{method_name}|query and data|{error}|query:{query}|bind_params:{bind_params}',
					array(
						'class_mame'=>__CLASS__,
						'method_name'=>__FUNCTION__,
						'error'=>$database->errorCode(),
						'query'=>$sql,
						'bind_params'=>json_encode($parameters)
					)
				);
			
			if ($query->execute($parameters)) {
				// fetchAll() is the PDO method that gets all result rows
		        $tips = $query->fetchAll();
				$row_count = $query->rowCount();
				
				if ($row_count >= 0)  {	
					return array('result'=>0, 'resultDesc'=>'Records retrieved successfully.', '_recordsRetrieved' => $row_count, '_totalRecords' => $total_records, 'tips'=>$tips );
				}
			} else {	
				$this->logger->error(
					'{class_mame}|{method_name}|error executing the query|{error}|query:{query}|bind_params:{bind_params}',
					array(
						'class_mame'=>__CLASS__,
						'method_name'=>__FUNCTION__,
						'error'=>$database->errorCode(),
						'query'=>$sql,
						'bind_params'=>json_encode($parameters)
					)
				);
				return  array('result' => 5, 'resultDesc' => 'Error executing a query.'); 
			}
		} catch (PDOException $e) {
			$this->logger->error(
				'{class_mame}|{method_name}|PDOException|{error}|{query}',
				array(
					'class_mame'=>__CLASS__,
					'method_name'=>__FUNCTION__,
					'error'=>$e->getMessage(),
					'query'=>$sql
				)
			);
			return  array('result' => 4, 'resultDesc' => 'Error executing a query. Error: '.$e->getMessage()); 
		}
		
		return array('result'=>1, 'resultDesc'=>'No records found', 'tips'=>$tips);
	} 
}
