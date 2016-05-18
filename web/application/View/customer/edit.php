<?php
use \Ssg\Core\Config;
?>
<span class="pull-right">
	<a class="btn btn-primary" href="<?php echo Config::get('URL'); ?>customer/index/" role="button">Back to All Customers</a> &nbsp;
    <a class="btn btn-info" href="<?php echo Config::get('URL'); ?>customer/view/<?=$this->id?>" role="button">View Customer</a>
</span>
<h1>Edit Customer Profile</h1>

<!-- echo out the system feedback (error and success messages) -->
<?php $this->renderFeedbackMessages(); ?>
<h3>Edit Customer Form</h3>


<form class="form-horizontal" method="post">
  <div class="form-group">
    <label for="msisdn" class="col-sm-2 control-label">Mobile Number:</label>
    <div class="col-sm-4">
      <input type="text" class="form-control input-sm" name="msisdn" id="msisdn" placeholder="Mobile Number" value="<?=$this->msisdn?>">
      <input type="hidden" id="id" name="id" value="<?=$this->id?>">
      <input type="hidden" id="action" name="action" value="add">
    </div>
    <div class="col-sm-6">
      <p>&nbsp;  </p>
    </div>
  </div>
   <div class="form-group">
    <label for="name" class="col-sm-2 control-label">Name</label>
    <div class="col-sm-4">
      <input type="text" class="form-control input-sm" name="name" id="name" placeholder="Name" value="<?= $this->name?>">
    </div>
    <div class="col-sm-6">
      <p>Customer Age e.g. <strong>30</strong> </p>
    </div>
  </div>
  <div class="form-group">
    <label for="age" class="col-sm-2 control-label">Age</label>
    <div class="col-sm-4">
      <input type="text" class="form-control input-sm" name="age" id="age" placeholder="Age" value="<?= $this->age?>">
    </div>
    <div class="col-sm-6">
      <p>Customer Age e.g. <strong>30</strong> </p>
    </div>
  </div>
  <div class="form-group">
    <label for="sex" class="col-sm-2 control-label">Sex</label>
    <div class="col-sm-4">
      <input type="text" class="form-control input-sm" name="sex" id="sex" placeholder="Gender" value="<?= $this->sex?>">
    </div>
    <div class="col-sm-6">
      <p>Customer gender e.g. <strong>M for Male or F for Female</strong> </p>
    </div>
  </div>
  <div class="form-group">
    <label for="location" class="col-sm-2 control-label">Location</label>
    <div class="col-sm-4">
      <input type="text" class="form-control input-sm" name="location" id="location" placeholder="Location" value="<?= $this->location?>">
    </div>
    <div class="col-sm-6">
      <p>Location e.g. <strong>Nairobi</strong> </p>
    </div>
  </div>
  <div class="form-group">
    <label for="reg_status" class="col-sm-2 control-label">Registration Status</label>
    <div class="col-sm-4">
      <input type="text" class="form-control input-sm" name="reg_status" id="reg_status" placeholder="Location" value="<?= $this->reg_status?>">
    </div>
    <div class="col-sm-6">
      <p>1- Initial, 2 - Pending, 3 - Confirmed/Complete </p>
    </div>
  </div>
  <div class="form-group">
    <label for="status" class="col-sm-2 control-label">Service Status</label>
    <div class="col-sm-4">
      <input type="text" class="form-control input-sm" name="status" id="status" placeholder="Location" value="<?= $this->status?>">
    </div>
    <div class="col-sm-6">
      <p>1- Active, 0 - Inactive </p>
    </div>
  </div>
   <div class="form-group">
    <label for="status_reason" class="col-sm-2 control-label">Status Change Reason</label>
    <div class="col-sm-4">
      <input type="text" class="form-control input-sm" name="status_reason" id="status_reason" placeholder="Location" value="<?= $this->status_reason?>">
    </div>
    <div class="col-sm-6">
      <p>Location e.g. <strong>Nairobi</strong> </p>
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-offset-2 col-sm-10">
      <button type="submit" name="submit" class="btn btn-default">Submit</button>
    </div>
  </div>
</form>