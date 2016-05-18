<?php
namespace Ssg\Model;

use \Ssg\Core\DatabaseFactory;
use Ssg\Core\Model;
use Ssg\Core\Session;
use Psr\Log\LoggerInterface;
use \stdClass;

/**
 * MessageModel - offers functions to manage messages in the system (sql, export, ext)
 *
 */
class CustomerModel extends Model
{
	/**
     * Construct this object by extending the basic Model class
     */
    public function __construct(LoggerInterface $logger = null)
    {
        parent::__construct($logger);
    }
	
	public function getCustomer($sub_id)
	{	
		//get the database connection
		$database=null;
		try {
			$database = DatabaseFactory::getFactory()->getConnection();
		} catch (Exception $ex) {
			return  array('result' => 3, 'resultDesc' => 'Cannot connect to the database. Error: '.$ex->getMessage(), 'customer' => new stdClass()); 
		}
		
		//prepare and execute the query
		try {		
			$sql = "SELECT * FROM tbl_subscribers WHERE id = :sub_id  ORDER BY id DESC LIMIT 1";
			$query = $database->prepare($sql);
			$bind_parameters = array(':sub_id' => $sub_id);
			
			if ($query->execute($bind_parameters)) {
				$customer = $query->fetch();
				if ($query->rowCount() < 1) {	
				
				   $this->logger->info(
						'{class_mame}|{method_name}|{sub_id}|record not found|{error}|{query}|bind_parameters:{bind_params}',
						array(
							'class_mame'=>__CLASS__,
							'method_name'=>__FUNCTION__,
							'sub_id'=>$sub_id,
							'error'=>$database->errorCode(),
							'query'=>$sql,
							'bind_params'=>json_encode($bind_parameters)
						)
					);
				   return array('result' => 1, 'resultDesc' => 'Record with id '.$sub_id.' not found.', 'customer' => new stdClass()); 
				}else{
					return array('result' => 0, 'resultDesc' => 'Record found.', 'customer' => $customer); 
				}
			} else {	
				$this->logger->error(
					'{class_mame}|{method_name}|{sub_id}|error executing the query|{error}|{query}|bind_parameters:{bind_params}',
					array(
						'class_mame'=>__CLASS__,
						'method_name'=>__FUNCTION__,
						'sub_id'=>$sub_id,
						'error'=>$database->errorCode(),
						'query'=>$sql,
						'bind_params'=>json_encode($bind_parameters)
					)
				);
				return  array('result' => 5, 'resultDesc' => 'Error executing a query.', 'customer' => new stdClass()); 
			}
		} catch (PDOException $e) {
			return  array('result' => 4, 'resultDesc' => 'Error executing a query. Error: '.$e->getMessage(), 'customer' => new stdClass()); 
		}
		
        return array('result' => 7, 'resultDesc' => 'Unknown error', 'customer' => new stdClass()); 
	}
	
	
	public function updateCustomer($customer)
	{	
		//initialize customer data
		$id='';
		$msisdn='';
		$name='';
		$age=0;
		$sex='';
		$location= '';
		$reg_status = 0;
		$status = 0;
		$status_reason = '';
		$last_updated_by= Session::get('user_id');
		
		
		//populate the data with the request data
		if(isset($customer->id)) $id=$customer->id;
		if(isset($customer->msisdn)) $msisdn=$customer->msisdn;
		if(isset($customer->name)) $name=$customer->name;
		if(isset($customer->age)) $age=$customer->age;	
		if(isset($customer->sex)) $sex=$customer->sex;	
		if(isset($customer->location)) $location=$customer->location;	
		if(isset($customer->reg_status)) $reg_status=$customer->reg_status;	
		if(isset($customer->status)) $status=$customer->status;	
		if(isset($customer->status_reason)) $status_reason=$customer->status_reason;	
		
		//check whether ther customer exists
		$query_result = self::getcustomer($id);
		
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
			$sql='UPDATE tbl_subscribers SET msisdn=:msisdn, name=:name, age = :age, sex=:sex, location=:location, reg_status	=:reg_status, status=:status, status_reason=:status_reason, last_updated_on=NOW(), last_updated_by = :last_updated_by WHERE id=:id';
			$query = $database->prepare($sql);
			
			$bind_patameters = array(':id' => $id, 
									':msisdn' => $msisdn , 
									':name' => $name, 
									':age' => $age,
									':sex' => $sex,
									':location' => $location,
									':reg_status' => $reg_status,
									':status' => $status, 
									':status_reason' => $status_reason, 
									':last_updated_by' => $last_updated_by);
			
			$this->logger->debug(
				'{class_mame}|{method_name}|query and data|{query}|bind_parameters:{bind_params}',
				array(
					'class_mame'=>__CLASS__,
					'method_name'=>__FUNCTION__,
					'query'=>$sql,
					'bind_params'=>json_encode($bind_patameters)
				)
			);	
			
			if ($query->execute($bind_patameters)) {
				$row_count = $query->rowCount();
				$errorCode = $database->errorCode();
				$database->commit();
				
				if ($row_count == 1) {	
					return array('result'=>0, 'resultDesc'=>'Record updated successfully.', 'customer'=>$customer);
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
		
		return array('result'=>1, 'resultDesc'=>'Updating records failed - '.$errorCode, 'customer'=>$customer);
	} 
	
	
	public function getCustomers($start_index=0, $limit=10, $msisdn='', $name='', $location='', $reg_status='-1', $status='-1', $sdp_status='-1', $order='DESC')
	{
        $sql = 'SELECT * FROM tbl_subscribers WHERE 1 ';
		
		$parameters = array();
		//include status filter
		if (isset($msisdn) && !empty($msisdn)) {
			$sql= $sql." AND msisdn=:msisdn";
			$parameters[':msisdn']=$msisdn;
		}
		
		//include text like filter
		if (isset($name) && !empty($name)) {
			$sql= $sql." AND name LIKE '%$name%'";
		}
		
		if (isset($location) && !empty($location)) {
			$sql= $sql." AND location=:location";
			$parameters[':location']=$location;
		}
		
		//include status filter
		if (isset($reg_status) && !(empty($reg_status)) && $reg_status!='-1') {
			$sql= $sql." AND reg_status=:reg_status";
			$parameters[':reg_status']=$reg_status;
		}
		
		//include status filter
		if (isset($status) &&  $status!='-1') {
			$sql= $sql." AND status=:status";
			$parameters[':status']=$status;
		}
		
		//include status filter
		if (isset($sdp_status) &&  $sdp_status!='-1') {
			$sql= $sql." AND sdp_status=:sdp_status";
			$parameters[':sdp_status']=$sdp_status;
		}
		
		$query_total = $sql; // copy query to be used to get the total number of reords (without the group by and limit clause)
		$sql= $sql.' ORDER BY id '.$order.' LIMIT '.$start_index.', '.$limit;
		
		
		// add some logic to handle exceptions in this script
		$row_count=0; 
		$total_records=0;
		$customers='';
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
		        $customers = $query->fetchAll();
				$row_count = $query->rowCount();
				
				if ($row_count >= 0)  {	
					return array('result'=>0, 'resultDesc'=>'Records retrieved successfully.', '_recordsRetrieved' => $row_count, '_totalRecords' => $total_records, 'customers'=>$customers );
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
		
		return array('result'=>1, 'resultDesc'=>'No records found', 'customers'=>$customers);
	} 
	
	
	
	public function getCustomerActivityLogs($sub_id, $msisdn, $start_index=0, $limit=10, $order='DESC')
	{
        $sql = 'SELECT * FROM tbl_activity_logs WHERE 1 ';
		
		$parameters = array();
		//include status filter
		if (isset($sub_id) && !empty($sub_id)) {
			$sql= $sql." AND (sub_id=:sub_id OR msisdn=:msisdn) ";
			$parameters[':sub_id']=$sub_id;
                        $parameters[':msisdn']=$msisdn;
		}
		
		$query_total = $sql; // copy query to be used to get the total number of reords (without the group by and limit clause)
		$sql= $sql.' ORDER BY id '.$order.' LIMIT '.$start_index.', '.$limit;
		
		
		// add some logic to handle exceptions in this script
		$row_count=0; 
		$total_records=0;
		$activities='';
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
		        $activities = $query->fetchAll();
				$row_count = $query->rowCount();
				
				if ($row_count >= 0)  {	
					return array('result'=>0, 'resultDesc'=>'Records retrieved successfully.', '_recordsRetrieved' => $row_count, '_totalRecords' => $total_records, 'activities'=>$activities );
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
		
		return array('result'=>1, 'resultDesc'=>'No records found', 'activities'=>$activities);
	} 
	
	public function getCustomerActivity($id)
	{	
		//get the database connection
		$database=null;
		try {
			$database = DatabaseFactory::getFactory()->getConnection();
		} catch (Exception $ex) {
			return  array('result' => 3, 'resultDesc' => 'Cannot connect to the database. Error: '.$ex->getMessage(), 'activity' => new stdClass()); 
		}
		
		//prepare and execute the query
		try {		
			$sql = "SELECT * FROM tbl_activity_logs WHERE id = :id  ORDER BY id DESC LIMIT 1";
			$query = $database->prepare($sql);
			$bind_parameters = array(':id' => $id);
			
			if ($query->execute($bind_parameters)) {
				$activity = $query->fetch();
				if ($query->rowCount() < 1) {	
				
				   $this->logger->info(
						'{class_mame}|{method_name}|{msisdn}|record not found|{error}|{query}|bind_parameters:{bind_params}',
						array(
							'class_mame'=>__CLASS__,
							'method_name'=>__FUNCTION__,
							'msisdn'=>$msisdn,
							'error'=>$database->errorCode(),
							'query'=>$sql,
							'bind_params'=>json_encode($bind_parameters)
						)
					);
				   return array('result' => 1, 'resultDesc' => 'Record with id '.$msidn.' not found.', 'activity' => new stdClass()); 
				}else{
					return array('result' => 0, 'resultDesc' => 'Record found.', 'activity' => $activity); 
				}
			} else {	
				$this->logger->error(
					'{class_mame}|{method_name}|{msisdn}|error executing the query|{error}|{query}|bind_parameters:{bind_params}',
					array(
						'class_mame'=>__CLASS__,
						'method_name'=>__FUNCTION__,
						'msisdn'=>$msisdn,
						'error'=>$database->errorCode(),
						'query'=>$sql,
						'bind_params'=>json_encode($bind_parameters)
					)
				);
				return  array('result' => 5, 'resultDesc' => 'Error executing a query.', 'activity' => new stdClass()); 
			}
		} catch (PDOException $e) {
			return  array('result' => 4, 'resultDesc' => 'Error executing a query. Error: '.$e->getMessage(), 'activity' => new stdClass()); 
		}
		
        return array('result' => 7, 'resultDesc' => 'Unknown error', 'activity' => new stdClass()); 
	}
	
	
}
