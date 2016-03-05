module.exports = {

	/**
   * Initializes a new UDP tranmission
   * @param {String} host - Target host to send the message
   * @param {Number} port - The target port
   * @param {Function} onSuccess - Callback to execute when initializes correctly
   * @param {Function} onError - Callback to execute when an error is fired
   */
	initialize: function(host, port, onSuccess, onError) {
		cordova.exec(
			onSuccess,
			onError,
			"UDPTransmit",
			"initialize",
			[host, port]
		);
		return true;
	},

  /**
   * Sends an UDP message to the initialized connection
   * @param {String} message - The message to send
   * @param {Function} onSuccess - Callback to execute when initializes correctly
   * @param {Function} onError - Callback to execute when an error is fired
   */
	sendMessage: function(message, onSuccess, onError) {
		cordova.exec(
			// To access the success and error callbacks for packet transmission, these two functions should be in your project:
			// UDPTransmissionSuccess(success)
			// UDPTransmissionError(error)
			onSuccess,
			onError,
			"UDPTransmit",
			"sendMessage",
			[message]
		);
		return true;
	},

  /**
   * Tries to resolve a host name
   * @param {String} hostName
   * @param {Function} onSuccess - Callback to execute when initializes correctly
   * @param {Function} onError - Callback to execute when an error is fired
   */
	resolveHostName: function(hostName, onSuccess, onError) {
		cordova.exec(
			onSuccess,
			onError,
			"UDPTransmit",
			"resolveHostName",
			[hostName]
		);
		return true;
	},
  
	enableBroadcast: function(onSuccess, onError) {
		cordova.exec(
			onSuccess,
			onError,
			"UDPTransmit",
			"enableBroadcast",
			[]
		);
	},
	
	onReceiveMessage: function(onSuccess, onError) {
		cordova.exec(
			onSuccess,
			onError,
			"UDPTransmit",
			"onReceiveMessage",
			[]
		);
	},
	
	onReceive: function(onSuccess, onError) {
		cordova.exec(
			onSuccess,
			onError,
			"UDPTransmit",
			"onReceive",
			[]
		);
	},

	close: function(onSuccess, onError) {
		cordova.exec(
			onSuccess,
			onError,
			"UDPTransmit",
			"close",
			[]
		);
	}

};