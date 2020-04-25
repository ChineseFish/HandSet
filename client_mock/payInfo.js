const mockServerClientInstance = process["mockServerClientInstance"];

mockServerClientInstance.mockAnyResponse({
  httpRequest: {
    "method": "GET",
    "path": "/payInfo",
    "queryStringParameters": {
      "busIdentifier": [".*"],
      "index": [".*"]
    }
  },
  "httpResponse": {
    "headers": {
      "Content-Type": ["application/json", "charset=utf-8"],
      "Last-Modified": ["Fri, 23 Oct 2019 07:28:00 GMT"],
    },
    "body": {
        "index": "100",
        "text": "Hello World"
    }
  }
}).then(() => {
  console.log("payInfo expectation created");
}).catch(e => {
  console.log(e);
});