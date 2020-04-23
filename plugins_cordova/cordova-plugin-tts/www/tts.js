var exec = require('cordova/exec');

module.exports = {

    /**
     * Send a payment request
     *
     * @example
     * <code>
     * tts.textToSpeech(text, function () {
     *     alert("Success");
     * }, function (reason) {
     *     alert("Failed: " + reason);
     * });
     * </code>
     */
    textToSpeech: function (text, onSuccess, onError) {
        return exec(onSuccess, onError, "tts", "textToSpeech", [text]);
    }
};
