/*
 * #%L
 * uCoinj :: UI Wicket
 * %%
 * Copyright (C) 2014 - 2016 EIS
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
/*!
 * uCoinj JavaScript Library v2.1.4
 * http://ucoinj.com/
 *
 * Includes Sizzle.js
 * http://sizzlejs.com/
 *
 * Copyright 2005, 2014 uCoinj Foundation, Inc. and other contributors
 * Released under the MIT license
 * http://ucoinj.org/license
 *
 * Date: 2015-04-28T16:01Z
 */

(function( global, factory ) {

	if ( typeof module === "object" && typeof module.exports === "object" ) {
		// For CommonJS and CommonJS-like environments where a proper `window`
		// is present, execute the factory and get uCoinj.
		// For environments that do not have a `window` with a `document`
		// (such as Node.js), expose a factory as module.exports.
		// This accentuates the need for the creation of a real `window`.
		// e.g. var uCoinj = require("ucoinj")(window);
		// See ticket #14549 for more info.
		module.exports = global.document ?
			factory( global, true ) :
			function( w ) {
				if ( !w.document ) {
					throw new Error( "uCoinj requires a window with a document" );
				}
				return factory( w );
			};
	} else {
		factory( global );
	}

// Pass this if window is not defined yet
}(typeof window !== "undefined" ? window : this, function( window, noGlobal ) {

// Support: Firefox 18+
// Can't be in strict mode, several libs including ASP.NET trace
// the stack via arguments.caller.callee and Firefox dies if
// you try to trace through "use strict" call chains. (#13335)
//

var
	// Use the correct document accordingly with window argument (sandbox)
	document = window.document,
    strundefined = typeof undefined,
	version = "1.0",
	nacl,
    scrypt,
    base58,
    base64,

	// Define a local copy of uCoinj
	uCoinj = function(document) {
		// The uCoinj object is actually just the init constructor 'enhanced'
		// Need init if uCoinj is called (just allow error to be thrown if not included)
		return new uCoinj.fn.init(document);
	};

uCoinj.fn = uCoinj.prototype = {
	// The current version of jQuery being used
	ucoinj: version,
	constructor: uCoinj,
    nacl: null,
    scrypt: null,
    base58: null,
    base64: null,
    wallet: {
         keypair: null,
         uid: null,
         pubkey: null
    },
    crypto_sign_BYTES: 64,
    SEED_LENGTH: 32, // Length of the key
    SCRYPT_PARAMS: {
          "N":4096,
          "r":16,
          "p":1
        }
};

uCoinj.fn.encode_utf8 = function(arr) {
    var i, s = [];
    for (i = 0; i < arr.length; i++) s.push(String.fromCharCode(arr[i]));
    return decodeURIComponent(escape(s.join('')));
};

uCoinj.fn.decode_utf8 = function(s) {
    var i, d = unescape(encodeURIComponent(s)), b = new Uint8Array(d.length);
    for (i = 0; i < d.length; i++) b[i] = d.charCodeAt(i);
    return b;
};

var
    // A central reference to the root jQuery(document)
    //rootuCoinj,
    wallet = uCoinj.fn.wallet,

    get_scrypt = function() {
        if (typeof module !== 'undefined' && module.exports) {
            // add node.js implementations
            require('scrypt-em');
            return scrypt_module_factory();
        }
        else if (scrypt_module_factory !== null){
            return scrypt_module_factory();
        }
        else {
            return setTimetout(get_scrypt, 100);
        }
    },

    get_nacl = function() {
        if (typeof module !== 'undefined' && module.exports) {
            // add node.js implementations
            require('nacl_factory');
            return nacl_factory.instantiate();
        }
        else if (nacl_factory !== null){
            return nacl_factory.instantiate();
        }
        else {
            return setTimetout(get_nacl, 100);
        }
    },

    get_base58 = function() {
        if (typeof module !== 'undefined' && module.exports) {
            // add node.js implementations
            require('base58');
            return Base58;
        }
        else if (Base58 !== null){
            return Base58;
        }
        else {
            return setTimetout(get_base58, 100);
        }
    },

    get_base64 = function() {
        if (typeof module !== 'undefined' && module.exports) {
            // add node.js implementations
            require('base58');
            return Base64;
        }
        else if (Base64 !== null){
            return Base64;
        }
        else {
            return setTimetout(get_base64, 100);
        }
    },

    // Constructor
    init = uCoinj.fn.init = function(document) {

        // load libraries
        scrypt = uCoinj.fn.scrypt = get_scrypt();
        nacl = uCoinj.fn.nacl = get_nacl();
        base58 = uCoinj.fn.base58 = get_base58();
        base64 = uCoinj.fn.base64 = get_base64();

        //var test = uCoinj.fn.test();
        //if (!test) {
        //    alert('Your navigator is not compatible: cryptographic features failed. Please report a bug.');
        //}
    },

    connect = uCoinj.fn.connect = function(salt, password) {
         var seed = uCoinj.fn.scrypt.crypto_scrypt(
                            uCoinj.fn.nacl.encode_utf8(password),
                            uCoinj.fn.nacl.encode_utf8(salt),
                            4096, 16, 1, 32 // see SCRYPT_PARAMS
                         );
         uCoinj.fn.wallet.keypair = uCoinj.fn.nacl.crypto_sign_keypair_from_seed(seed);
         uCoinj.fn.wallet.pubkey = uCoinj.fn.base58.encode(uCoinj.fn.wallet.keypair.signPk);
    },

    isConnected = uCoinj.fn.isConnected = function() {
        return uCoinj.fn.wallet.keypair !== null;
    }

    disconnect = uCoinj.fn.disconnect = function() {
        return uCoinj.fn.wallet.keypair !== null;
    },

    sign = uCoinj.fn.sign = function (message) {
        if (!isConnected()) {
            throw new Error('Not connected. Please connect using uCoinj().connect() method.')
        }
        var m = uCoinj.fn.decode_utf8(message);
        var sk = uCoinj.fn.wallet.keypair.signSk;
        var signedMsg = uCoinj.fn.nacl.crypto_sign(m, sk);
        var sig = new Uint8Array(uCoinj.fn.crypto_sign_BYTES);
        for (var i = 0; i < sig.length; i++) sig[i] = signedMsg[i];
        return uCoinj.fn.base64.encode(sig);
    },

    verify = uCoinj.fn.verify = function (message, signature, publicKey) {
        if (!isConnected()) {
            throw new Error('Not connected. Please connect using uCoinj().connect() method.')
        }

        var msg = uCoinj.fn.decode_utf8(message);
        var sig = uCoinj.fn.base64.decode(signature);
        var pub = uCoinj.fn.base58.decode(publicKey);
        var m = new Uint8Array(uCoinj.fn.crypto_sign_BYTES + msg.length);
        var sm = new Uint8Array(uCoinj.fn.crypto_sign_BYTES + msg.length);
        var i;
        for (i = 0; i < uCoinj.fn.crypto_sign_BYTES; i++) sm[i] = sig[i];
        for (i = 0; i < msg.length; i++) sm[i+uCoinj.fn.crypto_sign_BYTES] = msg[i];

        // Call to verification lib...
        var verified = uCoinj.fn.nacl.crypto_sign_open(sm, pub) !== null;

        return verified;
    },

    /**
    * Unit test methods: should return true
    */
    test = uCoinj.fn.test = function() {
        var result = true;
        var msg = 'my message to encrypt !';
        var expectedSignature = 'aAxVThibiZGbpJWrFo8MzZe8RDIoJ1gMC1UIr0utDBQilG44PjA/7o+pOoPAOXgDE3sosGeLHTw1Q/RhFBa4CA==';
        var expectedPubKey = 'G2CBgZBPLe6FSFUgpx2Jf1Aqsgta6iib3vmDRA1yLiqU';
        var expectedSecKey = '58LDg8QLmF5pv6Dn9h7X4yFKfMTdP8fdAiWVcyDoTRJu454fwRihCLULH4MW37zncsg4ruoTGJPZneWk22QmG1w4';
        connect('abc', 'def');
        if (!isConnected()) {
            console.log('[uCoinj] Test failed: Could not generate key pair.');
            return false;
        }
        if (wallet.pubkey !== expectedPubKey) {
            console.log('[uCoinj] Test failed: Bad public key, expected '+ expectedPubKey +' but get '+wallet.pubkey);
            result = false;
        }
        var secKey = base58.encode(wallet.keypair.signSk);
        if (secKey !== expectedSecKey) {
            console.log('[uCoinj] Test failed: Bad secret key, expected '+ expectedSecKey +' but get '+secKey);
            result = false;
        }

        var signature = sign(msg);
        if (signature !== expectedSignature) {
            console.log('[uCoinj] Test failed: Bad signature, expected '+ expectedSignature +' but get '+signature);
            result =false;
        }

        var verified = verify(msg, expectedSignature, wallet.pubkey);
        if (!verified) {
            console.log('[uCoinj] Test failed: signature verification failed, expected to be valid but return invalid');
            result = false;
        }
        return result;
    }
;



// Give the init function the jQuery prototype for later instantiation
init.prototype = uCoinj.fn;


// Initialize central reference
//rootjQuery = uCoinj( document );


// Register as a named AMD module, since uCoinj can be concatenated with other
// files that may use define, but not via a proper concatenation script that
// understands anonymous AMD modules. A named AMD is safest and most robust
// way to register. Lowercase ucoinj is used because AMD module names are
// derived from file names, and uCoinj is normally delivered in a lowercase
// file name. Do this after creating the global so that if an AMD module wants
// to call noConflict to hide this version of uCoinj, it will work.

// Note that for maximum portability, libraries that are not uCoinj should
// declare themselves as anonymous modules, and avoid setting a global if an
// AMD loader is present. uCoinj is a special case. For more information, see
// https://github.com/jrburke/requirejs/wiki/Updating-existing-libraries#wiki-anon

if ( typeof define === "function" && define.amd ) {
	define( "ucoinj", [], function() {
		return uCoinj;
	});
}


var
	// Map over uCoinj in case of overwrite
	_uCoinj = window.uCoinj,

	// Map over the $ in case of overwrite
	_uc = window.uc;

uCoinj.noConflict = function( deep ) {
	if ( window._uc === uCoinj ) {
		window.uc = _uc;
	}

	if ( deep && window.uCoinj === uCoinj ) {
		window.uCoinj = _uCoinj;
	}

	return uCoinj;
};

// Expose uCoinj and $ identifiers, even in AMD
// and CommonJS for browser emulators
if ( typeof noGlobal === strundefined ) {
	window.uCoinj = window.uc = uCoinj;
}




return uCoinj;

}));
