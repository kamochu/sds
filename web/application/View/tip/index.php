<?php 
use Ssg\Core\Config;
?>
<span class="pull-right">
	<a class="btn btn-primary" target="_blank" href="<?php echo Config::get('URL'); ?>tip/add/" role="button">Add New Tip</a> &nbsp;
</span>
<h1>Dating Tips Index</h1>

<!-- echo out the system feedback (error and success messages) -->
<?php $this->renderFeedbackMessages(); ?>

<h3>Query Filter:</h3>
        
<div class="form-group">
    <form class="form-inline">
    	<input type="text" class="form-control" name="text" id="text" placeholder="Text" value="<?=$this->text?>">
        <input type="text" class="form-control" name="status" id="status" placeholder="Tip Status" value="<?=$this->status?>">
      <button type="submit" class="btn btn-primary">Query</button>
    </form>
</div>

<table class="table table-striped">
	<thead>
    	<th>#</th>
        <th width="40%">Tip</th>
        <th>Effective Date</th>
        <th>Expiry Date</th>
        <th>Status</th>
        <th>Creation Date</th>
        <th>Manage Tip</th>
    </thead>
<?php 
$data = $this->result;
if ($data['_recordsRetrieved']>0) {	
	foreach ($data['tips'] as $tip) {
		?> 
        <tr>
            <td><?=$tip->id?></td>
            <td><a title="Click to view tip" href="<?php echo Config::get('URL'); ?>tip/view/<?=$tip->id?>"><?=$tip->tip?></a></td>
            <td><?=$tip->effective_date?></td>
            <td><?=$tip->expiry_date?></td>
            <td>
            <?php
            	if (null != Config::get('SERVICE_STATUSES')) {
					if ($tip->status == 1) {
						?>
						<span class="btn btn-success  btn-xs"><?php echo Config::get('SERVICE_STATUSES')[$tip->status]; ?></span>
						<?php
					} else {
						?>
						 <span class="btn btn-warning  btn-xs"><?php echo Config::get('SERVICE_STATUSES')[$tip->status]; ?></span>
						<?php
					}
					
				} else {
					echo $tip->status;
				}
			?>
            </td>
            <td><?=$tip->created_on?></td>
            <td><a title="Click to View, Edit, Delete Tip" href="<?php echo Config::get('URL'); ?>tip/view/<?=$tip->id?>"><span class="btn btn-primary  btn-xs">Open Tip</span></a>
        </tr>        
        <?php
	}
	
} else {
	?>
    <td colspan="7">No records found.</td>
    <?php
}
?>
</table>

<?php 
	echo $this->markup;
?>






