<?php
use \Ssg\Core\Config;
?>
<h1>Disable Dating Tip</h1>

<!-- echo out the system feedback (error and success messages) -->
<?php $this->renderFeedbackMessages(); ?>

<span class="pull-right" style="margin-bottom:30px">
	<a class="btn btn-primary" href="<?php echo Config::get('URL'); ?>tip/index/" role="button">All Tips</a> &nbsp;
	 <?php 
		if ($this->result['tip']->status == 1) {
			//on
			?>
            <a class="btn btn-warning" href="<?php echo Config::get('URL'); ?>tip/disable/<?=$this->result['tip']->id?>" role="button">Disable Tip</a> &nbsp;
			<?php
		} else {
			//off
			?>
             <a class="btn btn-success" href="<?php echo Config::get('URL'); ?>tip/enable/<?=$this->result['tip']->id?>" role="button">Enable Tip</a> &nbsp;
			<?php
		}
	?>
    <a class="btn btn-info" href="<?php echo Config::get('URL'); ?>tip/edit/<?=$this->result['tip']->id?>" role="button">Edit Tip</a> &nbsp;
    <a class="btn btn-danger" href="<?php echo Config::get('URL'); ?>tip/delete/<?=$this->result['tip']->id?>" role="button">Delete Tip</a>
</span>
<h3>Tip Information</h3>
<table class="table table-striped table-hover">
	<tr>
		<td width="15%"><strong>Tip ID:</strong> </td>
        <td><?=$this->result['tip']->id?> </td>
        <td width="15%"><strong>Status:</strong> </td>
        <td>
        <?php 
			if ($this->result['tip']->status == 1) {
				//on
				?>
                <span class="btn btn-success  btn-xs">ON</span>
                <?php
			} else {
				//off
				?>
                 <span class="btn btn-warning  btn-xs">OFF</span>
                <?php
			}
		?>
        </td>
    </tr>
	<tr>
		<td width="15%"><strong>Tip:</strong></td>
        <td colspan="3"><?=$this->result['tip']->tip?> </td>
    </tr>
    <tr>
		<td width="15%"><strong>Effective Date:</strong></td>
        <td><?=$this->result['tip']->effective_date?> </td>
        <td width="15%"><strong>Expiry Date:</strong></td>
        <td><?=$this->result['tip']->expiry_date?> </td>
    </tr>
     <tr>
		<td width="15%"><strong>Created On:</strong></td>
        <td><?=$this->result['tip']->created_on?> </td>
        <td width="15%"><strong>Last Updated On:</strong></td>
        <td><?=$this->result['tip']->last_updated_on?> </td>
    </tr>
    
</table>