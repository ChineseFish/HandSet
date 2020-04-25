const mockServerClientInstance = process["mockServerClientInstance"];

mockServerClientInstance.mockAnyResponse({
  httpRequest: {
    "method": "GET",
    "path": "/payInfo",
    'queryStringParameters': [
        {
            'index': 'test',
            'values': [ 'true' ]
        }
    ],
  },
  "httpResponse": {
    "headers": {
      "Content-Type": ["application/json", "charset=utf-8"],
      "Last-Modified": ["Fri, 23 Oct 2019 07:28:00 GMT"],
    },
    "body": {
        "index": 1,
        "text": 2
    }
  }
}).then(() => {
  console.log("busTicketList expectation created");
}).catch(e => {
  console.log(e);
});