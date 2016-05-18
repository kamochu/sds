<?php

use Ssg\Core\Config;
?>
<span class="pull-right">
    <a class="btn btn-primary" href="<?php echo Config::get('URL'); ?>task/submit/" role="button">Submit New Task</a>
</span>
<h1>Service (Matching) Tasks Index</h1>

<!-- echo out the system feedback (error and success messages) -->
<?php $this->renderFeedbackMessages(); ?>

<h3>Query Filter:</h3>

<div class="form-group">
    <form class="form-inline">
        <input type="text" class="form-control" name="start_date" id="start_date" placeholder="Start Date" value="<?= $this->start_date ?>">
        <input type="text" class="form-control" name="end_date" id="end_date" placeholder="End Date" value="<?= $this->end_date ?>">
        <input type="text" class="form-control" name="status" id="status" placeholder="Job Status" value="<?= $this->status ?>">
        <input type="text" class="form-control" name="initiator" id="initiator" placeholder="Initiator" value="<?= $this->initiator ?>">
        <button type="submit" class="btn btn-primary">Query</button>
    </form>
</div>

<table class="table table-striped">
    <thead>
    <th>#</th>
    <th>Job Date</th>
    <th>Batch Id</th>
    <th>All Records</th>
    <th>Dates</th>
    <th>Tips</th>
    <th>Info</th>
    <th>% SMS</th>
    <th>Job Status</th>
    <th>Initiator</th>
    <th>Comments</th>
    <th>Job Start Time</th>
    <th>Last Update Time</th>
</thead>
<?php
$data = $this->result;

if ($data['_recordsRetrieved'] > 0) {
    foreach ($data['tasks'] as $task) {
        ?> 
        <tr>
            <td><?= $task->id ?></td>
            <td><?= $task->job_date ?></td>
            <td><?= $task->batch_id ?></td>
            <td><?= $task->total_produced ?></td>
            <td><?= $task->no_date_matches ?></td>
            <td><?= $task->no_dating_tips ?></td>
            <td><?= $task->no_info_sms ?></td>
            <td><?= ($task->total_produced > 0) ? round(((($task->no_date_matches + $task->no_dating_tips + $task->no_info_sms) / $task->total_produced) * 100.0000), 2) . " %" : "0 %" ?></td>
            <td><?= $task->reason ?></td>
            <td><?= $task->initiator ?></td>
            <td><?= $task->initiator_comments ?></td>
            <td><?= $task->created_on ?></td>
            <td><?= $task->last_updated_on ?></td>
        </tr>        
        <?php
    }
} else {
    ?>
    <td colspan="13">No records found.</td>
    <?php
}
?>
</table>

<?php
echo $this->markup;
?>






