const mockServerClient = require('mockserver-client').mockServerClient;

//
const mockServerClientInstance = process["mockServerClientInstance"] = mockServerClient("localhost", 3000);

//
mockServerClientInstance.reset();

//
mockServerClientInstance.clear({
    'path': '/.*'
}).then(() => {
    console.log("cleared expectations that matches request matcher");

    require("./payInfo");
}).catch(e => {
    console.error(e);
})