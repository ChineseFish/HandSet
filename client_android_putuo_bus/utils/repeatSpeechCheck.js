const fs = require("fs");
const path = require("path");

const check1 = function(content)
{
    const reg = /pid=\d+\s+fetchPayInfo response: gtzn:\[(.+)\](.+?)gtzn:\[\1\]/gs;
    return content.match(reg)
}

const check2 = function(content)
{
    const reg = /gtzn:\[(?:.+)"text": "(.+)"(.+?"text": "\1".+)+\]/g;
    return content.match(reg)
}

const fileCheck = function(fileName)
{
    fs.readFile(path.join(__dirname, fileName), (err, data) => {
        if(!!err)
        {
            return console.error(err.toString())
        }
        
        //
        console.log('------------------------------------------');
        console.log(fileName);

        //
        console.log('------------------------------------------');
        console.log(check1(data.toString('utf-8')));

        //
        console.log('------------------------------------------');
        console.log(check2(data.toString('utf-8')));
        console.log('******************************************');
        console.log('******************************************');
    });
}
const options = process.argv;
const fileName = options[2];
if(fileName)
{
    fileCheck(fileName)
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
            fileCheck(file);
        }
    });
}