var exec = require('cordova/exec');

module.exports = {

    /**
     * Send a interval request
     *
     * @example
     * <code>
     * interval.setIndentifier(identifier, index, function () {
     *     alert("Success");
     * }, function (reason) {
     *     alert("Failed: " + reason);
     * });
     * </code>
     */
    setIndentifier: function (identifier, index, onSuccess, onError) {
        return exec(onSuccess, onError, "interval", "setIndentifier", [identifier, index]);
    }
};
