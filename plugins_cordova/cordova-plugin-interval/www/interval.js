var exec = require('cordova/exec');

module.exports = {

    /**
     * Send a interval request
     *
     * @example
     * <code>
     * interval.setIndentifier(identifier, function () {
     *     alert("Success");
     * }, function (reason) {
     *     alert("Failed: " + reason);
     * });
     * </code>
     */
    setIndentifier: function (identifier, onSuccess, onError) {
        return exec(onSuccess, onError, "interval", "setIndentifier", [identifier]);
    }
};