<?php
use \Ssg\Core\Config;
?>
<span class="pull-right">
	<a class="btn btn-primary" href="<?php echo Config::get('URL'); ?>tip/index/" role="button">All Tips</a>
</span>
<h1>Edit Dating Tip</h1>

<!-- echo out the system feedback (error and success messages) -->
<?php $this->renderFeedbackMessages(); ?>
<h3>Edit Dating Tip Form</h3>


<form class="form-horizontal" method="post">
  <div class="form-group">
    <label for="service_id" class="col-sm-2 control-label">Tip:</label>
    <div class="col-sm-4">
      <input type="text" class="form-control input-sm" name="text" id="text" placeholder="text" value="<?= $this->text?>">
      <input type="hidden" id="id" name="id" value="<?=$this->id?>">
      <input type="hidden" id="action" name="action" value="add">
    </div>
    <div class="col-sm-6">
      <p> &nbsp; </p>
    </div>
  </div>
  <div class="form-group">
    <label for="start_date" class="col-sm-2 control-label">Effective Date</label>
    <div class="col-sm-4">
      <input type="text" class="form-control input-sm" name="start_date" id="start_date" placeholder="Effective Date" value="<?= $this->start_date?>">
    </div>
    <div class="col-sm-6">
      <p>Effective date e.g. <strong>2015-09-01</strong> </p>
    </div>
  </div>
   <div class="form-group">
    <label for="end_date" class="col-sm-2 control-label">Expiry Date</label>
    <div class="col-sm-4">
      <input type="text" class="form-control input-sm" name="end_date" id="end_date" placeholder="Expiry Date" value="<?= $this->end_date?>">
    </div>
    <div class="col-sm-6">
      <p>Expiry date e.g. <strong>2037-01-01</strong> </p>
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-offset-2 col-sm-10">
      <button type="submit" name="submit" class="btn btn-default">Submit</button>
    </div>
  </div>
</form>