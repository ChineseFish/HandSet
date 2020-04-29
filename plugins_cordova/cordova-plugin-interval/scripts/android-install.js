#!/usr/bin/env node

module.exports = function (context) {
    let path = require('path'),
        fs = require('fs'),
        shell = require('shelljs'),
        projectRoot = context.opts.projectRoot,
        plugins = context.opts.plugins || [];

    // The plugins array will be empty during platform add
    if (plugins.length > 0 && plugins.indexOf('cordova-plugin-interval') === -1) {
        return;
    }

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
    console.info("Running android-install.Hook: " + context.hook + ", Package: " + packageName + ", Path: " + projectRoot + ".");

    if (!packageName) {
        return console.error("Package name could not be found!");
    }

    // android platform available?
    if (context.opts.cordova.platforms.indexOf("android") === -1) {
        return console.info("Android platform has not been added.");
    }

    //
    let targetDir  = path.join(projectRoot, "platforms", "android", "app", "src", "main", "java", "gtzn", "cordova", "interval");
    console.log(`targetDir: ${targetDir}`);

    //
    let targetFiles = ["Tts.java", "Remote.java", "Db.java"];

    if (['after_plugin_add', 'after_plugin_install'].indexOf(context.hook) === -1) {
        // remove it
        targetFiles.forEach(function (targetFile) {
            try {
                fs.unlinkSync(path.join(targetDir, targetFile));
            } catch (err) {
                
            }
        });
    } else {        
        // create directory
        if(!fs.existsSync())
        {
            shell.mkdir('-p', targetDir);
        }
        
        // sync the content
        targetFiles.forEach(function (targetFile) {
            fs.readFile(path.join(context.opts.plugin.dir, 'src', 'android', targetFile), {
                encoding: 'utf-8'
            }, function (err, data) {
                if (err) {
                    throw err;
                }

                data = data.replace(/__PACKAGE_NAME__/m, packageName);

                fs.writeFileSync(path.join(targetDir, targetFile), data);
            });
        });
    }
};
