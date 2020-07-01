const fs = require("fs");
const path = require("path");

const check1 = function(content)
{
    const reg = /gtzn:\[(.+)\](.+?)gtzn:\[\1\]/gs;
    return content.match(reg)
}

const check2 = function(content)
{
    const reg = /gtzn:\[(?:.+)"text": "(.+)"(.+?"text": "\1".+)+\]/g;
    return content.match(reg)
}


//
const options = process.argv;
const fileName = options[2];

//
fs.readFile(path.join(__dirname, fileName), (err, data) => {
    if(!!err)
    {
        return console.error(err.toString())
    }

    //
    let result = `------------------------------------------\n${fileName}\n------------------------------------------\n${check1(data.toString('utf-8'))}\n------------------------------------------\n${check2(data.toString('utf-8'))}\n******************************************\n******************************************\n`;

    //
    console.log(result);
});