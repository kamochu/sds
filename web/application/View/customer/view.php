<?php

use \Ssg\Core\Config;
?>
<h1>View Customer</h1>
<span class="pull-right" style="margin-bottom:30px">
    <a class="btn btn-primary" href="<?php echo Config::get('URL'); ?>customer/index/" role="button">All Customers</a> &nbsp;
    <a class="btn btn-info" href="<?php echo Config::get('URL'); ?>customer/edit/<?= $this->result['customer']->id ?>" role="button">Edit Customer</a>
</span>
<h3>Customer Information</h3>
<table class="table table-striped table-hover">
    <tr>
        <td width="20%"><strong>Customer ID:</strong> </td>
        <td><?= $this->result['customer']->id ?> </td>
        <td width="20%"><strong>Service Status:</strong> </td>
        <td>
            <?php
            if ($this->result['customer']->status == 1) {
                //on
                ?>
                <span class="btn btn-success  btn-xs">On</span>
                <?php
            } else {
                //off
                ?>
                <span class="btn btn-warning  btn-xs">Off</span>
                <?php
            }
            ?>
        </td>
    </tr>
    <tr>
        <td width="20%"><strong>Mobile Number:</strong></td>
        <td><?= $this->result['customer']->msisdn ?> </td>
        <td width="20%"><strong>Customer Name:</strong></td>
        <td><?= $this->result['customer']->name ?> </td>
    </tr>
    <tr>
        <td width="20%"><strong>Age:</strong></td>
        <td><?= $this->result['customer']->age ?> </td>
        <td width="20%"><strong>Sex:</strong></td>
        <td><?= $this->result['customer']->sex ?> </td>
    </tr>
    <tr>
        <td width="20%"><strong>Location:</strong></td>
        <td><?= $this->result['customer']->location ?> </td>
        <td width="20%"><strong>Looking For:</strong></td>
        <td>
            <?php
            if (null != Config::get('SERVICE_PREFERENCES')) {
                echo Config::get('SERVICE_PREFERENCES')[$this->result['customer']->preference];
            } else {
                echo $this->result['customer']->preference;
            }
            ?>
        </td>
    </tr>
    <tr>
        <td width="20%"><strong>Registration Status:</strong></td>
        <td>
            <?php
            if (null !== Config::get('REG_STATUSES')) {
                if ($this->result['customer']->reg_status == 5) {
                    ?>
                    <span class="btn btn-success  btn-xs"><?php echo Config::get('REG_STATUSES')[$this->result['customer']->reg_status]; ?></span>
                    <?php
                } else {
                    ?>
                    <span class="btn btn-warning  btn-xs"><?php echo Config::get('REG_STATUSES')[$this->result['customer']->reg_status]; ?></span>
                    <?php
                }
            } else {
                echo $this->result['customer']->reg_status;
            }
            ?>
        </td>
        <td width="20%"><strong>SDP Status:</strong></td>
        <td>
            <?php
            if (null != Config::get('SDP_STATUSES')) {
                if ($this->result['customer']->sdp_status == 1) {
                    ?>
                    <span class="btn btn-success  btn-xs"><?php echo Config::get('SDP_STATUSES')[$this->result['customer']->sdp_status]; ?></span>
                    <?php
                } else {
                    ?>
                    <span class="btn btn-warning  btn-xs"><?php echo Config::get('SDP_STATUSES')[$this->result['customer']->sdp_status]; ?></span>
                    <?php
                }
            } else {
                echo $this->result['customer']->sdp_status;
            }
            ?>
        </td>
    </tr>
    <tr>
        <td width="20%"><strong>SDP Service ID:</strong></td>
        <td><?= $this->result['customer']->service_id ?> </td>
        <td width="20%"><strong>SDP Product ID:</strong></td>
        <td><?= $this->result['customer']->product_id ?> </td>
    </tr>
    <tr>
        <td width="20%"><strong>Created On:</strong></td>
        <td><?= $this->result['customer']->created_on ?> </td>
        <td width="20%"><strong>Last Updated On:</strong></td>
        <td><?= $this->result['customer']->last_updated_on ?> </td>
    </tr>
    <tr>
        <td width="20%"><strong>Subscription Effective Date:</strong></td>
        <td><?= $this->result['customer']->effective_time ?> </td>
        <td width="20%"><strong>Subscription Expiry Date:</strong></td>
        <td><?= $this->result['customer']->expiry_time ?> </td>
    </tr>
    <tr>
        <td width="20%"><strong>Last Shared On:</strong></td>
        <td><?= $this->result['customer']->last_shared_on ?> </td>
        <td width="20%"><strong>Last Updated On:</strong></td>
        <td><?= $this->result['customer']->last_updated_on ?> </td>
    </tr>

</table>

<div style="height:1px;"> </div> <!-- spacer -->

<h3>Customer Activities</h3>


<table class="table table-striped">
    <thead>
    <th>#</th>
    <th>Req. Type</th>
    <th>Operation</th>
    <th>Incoming Msg</th>
    <th>Outgoing Msg</th>
    <th>Send Status</th>
    <th>Delivery Status</th>
    <th>Activity Date</th>
</thead>

<?php
if ($this->total_records > 0) {
    foreach ($this->activities as $activity) {
        ?>
        <tr>
            <td><?= $activity->id ?></td>
            <td>
                <?php
                if (null != Config::get('REQUEST_TYPES')) {
                    echo Config::get('REQUEST_TYPES')[$activity->request_type]; 
                } else {
                    echo $activity->request_type;
                }
                ?>

            </td>
            <td>
                <?php
                if (null != Config::get('OPERATIONS')) {
                    echo Config::get('OPERATIONS')[$activity->operation]; 
                } else {
                    echo $activity->operation;
                }
                ?>
            </td>
            <td width="12%"><?= $activity->in_message_text ?></td>
            <td width="28%"><?= $activity->out_message_text ?></td>
            <td><?= $activity->out_send_status ?></td>
            <td><?= $activity->out_delivery_status ?></td>
            <td><?= $activity->created_on ?></td>
        </tr>
        <?php
    }
} else {
    ?>
    <td colspan="8">No records found.</td>
    <?php
}
?>

</table>


<?php
echo $this->markup;
?>
