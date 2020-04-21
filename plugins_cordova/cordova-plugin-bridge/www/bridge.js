var exec = require('cordova/exec');

module.exports = {

    /**
     * Send a payment request
     *
     * @example
     * <code>
     * CCBPay.jhNativeSDKPay(order, price, function () {
     *     alert("Success");
     * }, function (reason) {
     *     alert("Failed: " + reason);
     * });
     * </code>
     */
    jhNativeSDKPay: function (order, price, onSuccess, onError) {
        return exec(onSuccess, onError, "CCBPay", "jhNativeSDKPay", [order, price]);
    }
};
