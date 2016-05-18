<?php

use Ssg\Core\Config;
?>

<span class="pull-right" style="margin-bottom:30px">
    <a class="btn btn-primary" href="<?php echo Config::get('URL'); ?>task/index/" role="button">Go Back to All Tasks</a> &nbsp;
</span>

<h1>Submit System Task</h1>

<!-- echo out the system feedback (error and success messages) -->
<?php $this->renderFeedbackMessages(); ?>

<p>&nbsp;</p>

<h3>Submit System Task Form</h3> 
<form class="form-horizontal" method="post">
    <div class="form-group">
        <label for="msisdn" class="col-sm-2 control-label">Submission Reason:</label>
        <div class="col-sm-4">
            <input type="text" class="form-control input-sm" name="reason" id="reason" placeholder="reason" value="<?= $this->reason ?>">
            <input type="hidden" id="action" name="action" value="add">
        </div>
        <div class="col-sm-6">
            <p>Reason for submitting the system task e.g. <strong>re-run after fix</strong> </p>
        </div>
    </div>

    <div class="form-group">
        <div class="col-sm-offset-2 col-sm-10">
            <button type="submit" name="submit" class="btn btn-default">Submit</button>
        </div>
    </div>
</form>

<div style="height: 30px"></div>
<div class="alert alert-warning" role="alert"><strong>Submission of system tasks takes a lot of system resources. We recommend doing it during low traffic hours. If you are sure you really need to do this, please proceed and submit.</strong></div>
