const fs = require("fs");
const path = require("path");
const { fork } = require('child_process');

//
const options = process.argv;
const fileName = options[2];
if(fileName)
{
    fork(path.join(__dirname, "check.js"), [fileName]);
}
else
{
    fs.readdir(__dirname, (err, files) => {
        if(!!err)
        {
            return console.error(err.toString())
        }

        //
        for(let file of files)
        {
            if(file.search(/\.txt/) < 0)
            {
                continue;
            }

            if(file === 'test.txt')
            {
                continue;
            }

            //
            fork(path.join(__dirname, "check.js"), [file]);
        }
    });
}