<?php

/**
 * Configuration for DEVELOPMENT environment
 * To create another configuration set just copy this file to config.production.php etc. You get the idea :)
 */
/**
 * Configuration for: Error reporting
 * Useful to show every little problem during development, but only show hard / no errors in production.
 * It's a little bit dirty to put this here, but who cares. For development purposes it's totally okay.
 * 
 * ini_set does only work if that code is executed. Not useful for code that has parse errors because the 
 * error will be before the code is executed. Instead write those changes into the php.ini.
 */
// Report all errors
error_reporting(E_ALL);
// DO NOT display errors on front end 
ini_set("display_errors", 0);
// log the errors 
ini_set("log_errors", 1);
//redirect the errors to a log file
ini_set("error_log", realpath(dirname(__FILE__) . '/../../') . '/logs/php_error_' . date('Ymd') . '.log');

/**
 * Returns the full configuration.
 * This is used by the core/Config class.
 */
return array(
    /**
     * System version configuration
     *
     */
    'APP_NAME' => 'SMS Dating Service Portal',
    'VERSION' => 'SDS_v1.0.0',
    'COPYRIGHT' => '&copy; ' . date('Y') . ' SDS. All rights reserved.',
    /**
     * The IP address allowed to access the solution
     * Add a new line for every IP that you ant to allow
     */
    'ALLOWED_IPS' => array(
        '::1',
        'localhost', // local access
        '127.0.0.1', // local access
        '196.201.216.13', // production SDP
        '196.201.216.14', // production SDP
    ),
    /**
     * 
     * 
     */
    'REG_STATUSES' => array(
	'0' => 'Waiting',
        '1' => 'Initial',
        '2' => 'Pending',
	'3' => 'Basic',
	'4' => 'Advanced',
        '5' => 'Complete',
    ),
    'SERVICE_STATUSES' => array(
        '1' => 'Active',
        '0' => 'Inactive',
    ),
    'SDP_STATUSES' => array(
        '1' => 'On',
        '0' => 'Off',
    ),
    'SERVICE_PREFERENCES' => array(
        '1' => 'Friend',
        '2' => 'Partner',
        '3' => 'Sex',
    ),
    'REQUEST_TYPES' => array(
        '1' => 'Inbound SMS',
        '2' => 'Subscription',
        '3' => 'Operator',
        '4' => 'System',
        '98' => 'Delivery',
        '99' => 'Other',
    ),
    'OPERATIONS' => array(
        '1' => 'Register',
        '2' => 'Update Details',
        '3' => 'Update Preference',
        '4' => 'Pause',
        '5' => 'Resume',
        '6' => 'Opt Out',
        '7' => 'Info',
        '8' => 'Daily SMS',
        '99' => 'Other',
    ),
    /**
     * Configuration for: Base URL
     * This detects your URL/IP incl. sub-folder automatically. You can also deactivate auto-detection and provide the
     * URL manually. This should then look like 'http://192.168.33.44/' ! Note the slash in the end.
     */
    'URL' => 'http://' . $_SERVER['HTTP_HOST'] . str_replace('public', '', dirname($_SERVER['SCRIPT_NAME'])),
    'URL_RES' => 'http://' . $_SERVER['HTTP_HOST'] .'/res/',
    /**
     * Configuration for: Folders
     * Usually there's no reason to change this.
     */
    'PATH_CLASS' => realpath(dirname(__FILE__) . '/../../') . '/application/Core/',
    'PATH_CONTROLLER' => realpath(dirname(__FILE__) . '/../../') . '/application/Controller/',
    'PATH_VIEW' => realpath(dirname(__FILE__) . '/../../') . '/application/View/',
    //dirname(__FILE__).'/../../vendor/log4php/Logger.php';
    /**
     * Configuration for: logger interface
     * Used by the log4php library
     */
    'LOG4PHP_CONFIG' => realpath(dirname(__FILE__)) . '/log4php_config.xml',
    'LOG4PHP_LOGGER_FILE' => realpath(dirname(__FILE__) . '/../../') . '/vendor/log4php/Logger.php',
    /**
     * Configuration for: Avatar paths
     * Internal path to save avatars. Make sure this folder is writable. The slash at the end is VERY important!
     */
    'PATH_AVATARS' => realpath(dirname(__FILE__) . '/../../') . '/public/avatars/',
    'PATH_AVATARS_PUBLIC' => 'avatars/',
    /**
     * Configuration for: Default controller and action
     */
    'DEFAULT_CONTROLLER' => 'index',
    'DEFAULT_ACTION' => 'index',
    /**
     * Configuration for: Database
     * DB_TYPE The used database type. Note that other types than "mysql" might break the db construction currently.
     * DB_HOST The mysql hostname, usually localhost or 127.0.0.1
     * DB_NAME The database name
     * DB_USER The username
     * DB_PASS The password
     * DB_PORT The mysql port, 3306 by default (?), find out via phpinfo() and look for mysqli.default_port.
     * DB_CHARSET The charset, necessary for security reasons. Check Database.php class for more info.
     */
    'DB_TYPE' => 'mysql',
    'DB_HOST' => 'localhost',
    'DB_NAME' => 'sds',
    'DB_USER' => 'app',
    'DB_PASS' => '!QAZ2wsx',
    'DB_PORT' => '3306',
    'DB_CHARSET' => 'utf8',
    /**
     * Configuration for: Additional login providers: Facebook
     * CURRENTLY REMOVED (as Facebook has removed support for the used API version).
     * Another, better and up-to-date implementation might come soon.
     */
    'FACEBOOK_LOGIN' => false,
    /**
     * Configuration for: Captcha size
     * The currently used Captcha generator (https://github.com/Gregwar/Captcha) also runs without giving a size,
     * so feel free to use ->build(); inside CaptchaModel.
     */
    'CAPTCHA_WIDTH' => 359,
    'CAPTCHA_HEIGHT' => 100,
    /**
     * Configuration for: Cookies
     * 1209600 seconds = 2 weeks
     * COOKIE_PATH is the path the cookie is valid on, usually "/" to make it valid on the whole domain.
     * @see http://stackoverflow.com/q/9618217/1114320
     * @see php.net/manual/en/function.setcookie.php
     */
    'COOKIE_RUNTIME' => 1209600,
    'COOKIE_PATH' => '/',
    /**
     * Configuration for: Avatars/Gravatar support
     * Set to true if you want to use "Gravatar(s)", a service that automatically gets avatar pictures via using email
     * addresses of users by requesting images from the gravatar.com API. Set to false to use own locally saved avatars.
     * AVATAR_SIZE set the pixel size of avatars/gravatars (will be 44x44 by default). Avatars are always squares.
     * AVATAR_DEFAULT_IMAGE is the default image in public/avatars/
     */
    'USE_GRAVATAR' => false,
    'GRAVATAR_DEFAULT_IMAGESET' => 'mm',
    'GRAVATAR_RATING' => 'pg',
    'AVATAR_SIZE' => 44,
    'AVATAR_JPEG_QUALITY' => 85,
    'AVATAR_DEFAULT_IMAGE' => 'default.jpg',
    /**
     * Configuration for: Email server credentials
     *
     * Here you can define how you want to send emails.
     * If you have successfully set up a mail server on your linux server and you know
     * what you do, then you can skip this section. Otherwise please set EMAIL_USE_SMTP to true
     * and fill in your SMTP provider account data.
     *
     * EMAIL_USED_MAILER: Check Mail class for alternatives
     * EMAIL_USE_SMTP: Use SMTP or not
     * EMAIL_SMTP_AUTH: leave this true unless your SMTP service does not need authentication
     */
    'EMAIL_USED_MAILER' => 'phpmailer',
    'EMAIL_USE_SMTP' => false,
    'EMAIL_SMTP_HOST' => 'yourhost',
    'EMAIL_SMTP_AUTH' => true,
    'EMAIL_SMTP_USERNAME' => 'yourusername',
    'EMAIL_SMTP_PASSWORD' => 'yourpassword',
    'EMAIL_SMTP_PORT' => 465,
    'EMAIL_SMTP_ENCRYPTION' => 'ssl',
    /**
     * Configuration for: Email content data
     */
    'EMAIL_PASSWORD_RESET_URL' => 'login/verifypasswordreset',
    'EMAIL_PASSWORD_RESET_FROM_EMAIL' => 'no-reply@example.com',
    'EMAIL_PASSWORD_RESET_FROM_NAME' => 'My Project',
    'EMAIL_PASSWORD_RESET_SUBJECT' => 'Password reset for PROJECT XY',
    'EMAIL_PASSWORD_RESET_CONTENT' => 'Please click on this link to reset your password: ',
    'EMAIL_VERIFICATION_URL' => 'login/verify',
    'EMAIL_VERIFICATION_FROM_EMAIL' => 'no-reply@example.com',
    'EMAIL_VERIFICATION_FROM_NAME' => 'My Project',
    'EMAIL_VERIFICATION_SUBJECT' => 'Account activation for PROJECT XY',
    'EMAIL_VERIFICATION_CONTENT' => 'Please click on this link to activate your account: ',
    /**
     * Table display data
     */
    'RECORDS_PER_PAGE' => '50',
    'CRUMBS' => '20',
    'MAX_RECORDS_PDF' => '500',
    /**
     * Batch Job configurations
     *
     */
    'MATCH_JOB_SCRIPT' => '/bin/sh /root/sds_backend/bin/sds_matcher_bg.sh',
    /**
      /**
     * Configuration for: SP SDP data
     */
    'SP_ID' => '601626',
    'SP_PASSWORD' => 'Discovery2015#',
    /**
     * Configuration for: SDP SendSms configurations
     */
    'SEND_SMS_DEFAULT_SERVICE_ENDPOINT' => 'http://196.201.216.13:8310/SendSmsService/services/SendSms',
    'SEND_SMS_DEFAULT_DELIVERY_NOTIFICATION_FLAG' => 1,
    'SEND_SMS_DEFAULT_DELIVERY_NOTIFICATION_ENDPOINT' => 'http://216.172.165.211/test/',
    'SEND_SMS_MAXIMUM_RECIPIENTS' => 1,
    /**
     * Configuration for: SDP Get Sms Delivery Status configurations
     */
    'GET_DELIVERY_STATUS_DEFAULT_SERVICE_ENDPOINT' => 'http://196.201.216.13:8310/SendSmsService/services/SendSms',
    /**
     * Configuration for: SDP SmsNotificationManager configurations
     */
    'SMS_NOTIFICATION_MANAGER_ENDPOINT' => 'http://196.201.216.13:8310/SmsNotificationManagerService/services/SmsNotificationManager',
    /**
     * Configuration for: Service Manager configurations
     * SMS_ON_DEMAND_SERVICE_TYPE on demand service type id, confirm the database configurations
     */
    'SMS_ON_DEMAND_SERVICE_TYPE' => 1,
    'SMS_SERVICE_ON' => 1,
    'SMS_SERVICE_OFF' => 0,
);
