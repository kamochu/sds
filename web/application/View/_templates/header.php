<?php
use \Ssg\Core\Config;
use \Ssg\Core\Session;
?>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="../../favicon.ico">

    <title><?php echo Config::get('APP_NAME'); ?></title>

    <!-- Bootstrap core CSS -->
    <link href="<?php echo Config::get('URL_RES'); ?>css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="<?php echo Config::get('URL_RES'); ?>css/custom.css" rel="stylesheet">
    <link href="<?php echo Config::get('URL_RES'); ?>css/pagination.css" rel="stylesheet">
    <link href="<?php echo Config::get('URL_RES'); ?>js/jtable/themes/redmond/jquery-ui-1.8.16.custom.css" rel="stylesheet" type="text/css" />

    <!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
    <!--[if lt IE 9]><script src="<?php echo Config::get('URL_RES'); ?>assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
    <script src="<?php echo Config::get('URL_RES'); ?>assets/js/ie-emulation-modes-warning.js"></script>

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>
<body>

	<!-- Static navbar -->
    <nav class="navbar navbar-default navbar-static-top">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="<?php echo Config::get('URL'); ?>"><?php echo Config::get('APP_NAME'); ?></a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
        
        	<?php 
			if (Session::userIsLoggedIn()) {
				?>
                  <ul class="nav navbar-nav">
                    <li><a href="<?php echo Config::get('URL'); ?>customer/index/">Customers</a></li>
                    <li><a href="<?php echo Config::get('URL'); ?>tip/index/">Dating Tips</a></li>
                    <li><a href="<?php echo Config::get('URL'); ?>task/index/">System Tasks</a></li>
                    <!--<li><a href="<?php echo Config::get('URL'); ?>report/index/">Reports</a></li>-->
                  </ul>
                  <ul class="nav navbar-nav navbar-right">
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><span class="glyphicon glyphicon-user" aria-hidden="true"></span> My Account</a>
                        <ul class="dropdown-menu" role="menu">
                            <li class="active"><a href="<?php echo Config::get('URL'); ?>login/logout/">Logout <strong><?= Session::get('user_name') ?></strong></a></li>
                            <li><a href="<?php echo Config::get('URL'); ?>login/changePassword/">Change Password</a></li>
                        </ul>
                    </li>
                  </ul>
                <?php
			}
			?>
        
          
          
        </div><!--/.nav-collapse -->
      </div>
    </nav>
    
    <div class="container">
    	<div class="min-height-200px">
        	<!--end of header and beginning of page body -->
    

       
