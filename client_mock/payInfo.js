const mockServerClientInstance = process["mockServerClientInstance"];

mockServerClientInstance.mockAnyResponse({
  httpRequest: {
    "method": "GET",
    "path": "/fetchInitIndex",
    "queryStringParameters": {
      "busIdentifier": [".*"]
    }
  },
  "httpResponse": {
    "headers": {
      "Content-Type": ["application/json", "charset=utf-8"],
      "Last-Modified": ["Fri, 23 Oct 2019 07:28:00 GMT"],
    },
    "body": {
        "index": "0"
    }
  }
}).then(() => {
  console.log("payInfo expectation created");
}).catch(e => {
  console.log(e);
});

mockServerClientInstance.mockAnyResponse({
  httpRequest: {
    "method": "GET",
    "path": "/ashx/GetPlayVoice.ashx",
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
    "body": [
      {
        "index": "7",
        "text": "a2"
      },
      {
        "index": "8",
        "text": "a3"
      }
    ]
  }
}).then(() => {
  console.log("payInfo expectation created");
}).catch(e => {
  console.log(e);
});