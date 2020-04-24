#!/usr/bin/env node

module.exports = function (context) {
    let path = require('path'),
        fs = require('fs'),
        shell = require('shelljs'),
        projectRoot = context.opts.projectRoot;

    // parse cordova config.xml
    let ConfigParser = null;
    try {
        ConfigParser = context.requireCordovaModule('cordova-common').ConfigParser;
    } catch(e) {
        // fallback
        ConfigParser = context.requireCordovaModule('cordova-lib/src/configparser/ConfigParser');
    }

    // fetch android package name
    let config = new ConfigParser(path.join(context.opts.projectRoot, "config.xml")),
        packageName = config.android_packageName() || config.packageName();

    // replace dash (-) with underscore (_)
    packageName = packageName.replace(/-/g , "_");
    
    //
    console.info("Running after-prepare.Hook: " + context.hook + ", Package: " + packageName + ", Path: " + projectRoot + ".");

    if (!packageName) {
        return console.error("Package name could not be found!");
    }

    // android platform available?
    if (context.opts.cordova.platforms.indexOf("android") === -1) {
        return console.info("Android platform has not been added.");
    }

    /********************************************* 复制包（替换包名） *********************************************/
    targetDir  = path.join(projectRoot, "platforms", "android", "app", "src", "main", "java", packageName.replace(/\./g, path.sep));
    console.log(targetDir);

    let targetFiles = ["MainActivity.java"];

    // create directory
    if(!fs.existsSync())
    {
        shell.mkdir('-p', targetDir);
    }
    
    // sync the content
    targetFiles.forEach(function (targetFile) {
        fs.readFile(path.join(__dirname, targetFile), {
            encoding: 'utf-8'
        }, function (err, data) {
            if (err) {
                throw err;
            }

            data = data.replace(/^package __PACKAGE_NAME__;/m, 'package ' + packageName + ";");
            fs.writeFileSync(path.join(targetDir, targetFile), data);
        });
    });


    /********************************************* 复制包（不替换包名） *********************************************/
    // modify build.gradle
    shell.cp(
        path.join(__dirname, "build-extras.gradle"), 
        path.join(projectRoot, "platforms", "android", "app")
    );
    
    // modify gradle properties
    shell.cp(
        '-rf',
        path.join(__dirname, "gradle.properties"), 
        path.join(projectRoot, "platforms", "android")
    );
    
    // modify cordova WebView implementation
    shell.cp(
        '-rf',
        path.join(__dirname, "SystemWebViewClient.java"), 
        path.join(projectRoot, "platforms", "android", "CordovaLib", "src", "org", "apache", "cordova", "engine")
    );
};
