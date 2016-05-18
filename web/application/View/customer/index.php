<?php 
use Ssg\Core\Config;
?>
<span class="pull-right">
	<a class="btn btn-primary" target="_blank" href="<?php echo Config::get('URL'); ?>customer/pdf/" role="button">Print PDF</a>
</span>
<h1>Customers Index</h1>

<!-- echo out the system feedback (error and success messages) -->
<?php $this->renderFeedbackMessages(); ?>

<h3>Query Filter:</h3>
        
<div class="form-group">
    <form class="form-inline">
    	<input type="text" class="form-control" name="msisdn" id="msisdn" placeholder="Mobile Number" value="<?=$this->msisdn?>">
        <input type="text" class="form-control" name="name" id="name" placeholder="Name" value="<?=$this->name?>">
        <input type="text" class="form-control small-control" name="location" id="location" placeholder="Location" value="<?=$this->location?>">
        <input type="text" class="form-control" name="reg_status" id="reg_status" placeholder="Reg Status" value="<?=$this->reg_status?>">
        <input type="text" class="form-control" name="status" id="status" placeholder="Service Status" value="<?=$this->status?>">
        <input type="text" class="form-control" name="sdp_status" id="sdp_status" placeholder="SDP Status" value="<?=$this->sdp_status?>">
      <button type="submit" class="btn btn-primary">Query</button>
    </form>
</div>

<table class="table table-striped">
	<thead>
    	<th>#</th>
        <th>Mobile #</th>
        <th>Name</th>
        <th>Age</th>
        <th>Sex</th>
        <th>Location</th>
        <th>Reg Status</th>
        <th>SDP Status</th>
        <th>Service Status</th>
        <th>Preference</th>
        <th>Reg Date</th>
        <th>Manage</th>
    </thead>
<?php 
$data = $this->result;
if ($data['_recordsRetrieved']>0) {	
	foreach ($data['customers'] as $customer) {
		?> 
        <tr>
            <td><?=$customer->id?></td>
            <td><a title="Click to view customer" href="<?php echo Config::get('URL'); ?>customer/view/<?=$customer->id?>"><?=$customer->msisdn?></a></td>
            <td><a title="Click to view customer" href="<?php echo Config::get('URL'); ?>customer/view/<?=$customer->id?>"><?=$customer->name?></a></td>
            <td><?=$customer->age?></td>
            <td><?=$customer->sex?></td>
            <td><?=$customer->location?></td>
			<td>
			<?php
            	if (null !== Config::get('REG_STATUSES')) {
					if ($customer->reg_status == 5) {
						?>
						<span class="btn btn-success  btn-xs"><?php echo Config::get('REG_STATUSES')[$customer->reg_status]; ?></span>
						<?php
					} else {
						?>
						 <span class="btn btn-warning  btn-xs"><?php echo Config::get('REG_STATUSES')[$customer->reg_status]; ?></span>
						<?php
					}
					
				} else {
					echo $customer->reg_status;
				}
			?>
            </td>
            <td>
            <?php
            	if (null != Config::get('SDP_STATUSES')) {
					if ($customer->sdp_status == 1) {
						?>
						<span class="btn btn-success  btn-xs"><?php echo Config::get('SDP_STATUSES')[$customer->sdp_status]; ?></span>
						<?php
					} else {
						?>
						 <span class="btn btn-warning  btn-xs"><?php echo Config::get('SDP_STATUSES')[$customer->sdp_status]; ?></span>
						<?php
					}
					
				} else {
					echo $customer->sdp_status;
				}
			?>
            </td>
            <td>
            <?php
            	if (null != Config::get('SERVICE_STATUSES')) {
					if ($customer->status == 1) {
						?>
						<span class="btn btn-success  btn-xs"><?php echo Config::get('SERVICE_STATUSES')[$customer->status]; ?></span>
						<?php
					} else {
						?>
						 <span class="btn btn-warning  btn-xs"><?php echo Config::get('SERVICE_STATUSES')[$customer->status]; ?></span>
						<?php
					}
					
				} else {
					echo $customer->status;
				}
			?>
            </td>
            <td>
            <?php
            	if (null != Config::get('SERVICE_PREFERENCES')) {
					echo Config::get('SERVICE_PREFERENCES')[$customer->preference];
				} else {
					echo $customer->preference;
				}
			?>
            </td>
            <td><?=$customer->created_on?></td>
            <td><a href="<?php echo Config::get('URL'); ?>customer/view/<?=$customer->id?>"><span class="btn btn-primary  btn-xs">Open Customer</span></a>
        </tr>        
        <?php
	}
	
} else {
	?>
    <td colspan="11">No records found.</td>
    <?php
}
?>
</table>

<?php 
	echo $this->markup;
?>






