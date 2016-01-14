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
(function() {
    var app = (typeof module !== "undefined" && module !== null ? module.exports : void 0) || (window.app = {});

    var
      ucoinj,

      init = app.init = function() {
        ucoinj = uCoinj(document);
      },

      login = app.login = function(event) {
        var salt = $('#localSalt').val();
        var password = $('#localPassword').val();
        var challengeMessage = $('#challengeMessage').val();

        if (salt === "" || password === "" || challengeMessage === "") {
           event.preventDefault();
           return;
        }

        var challengeMessage = $('#challengeMessage').val();
        ucoinj.connect(salt, password);
        var sign = ucoinj.sign(challengeMessage);

        $('#username').val(ucoinj.wallet.pubkey);
        $('#password').val(sign + '|' + challengeMessage);
        $('#form').submit();
     }
  ;

}).call(this);

$( document ).ready(function() {
    app.init();
});
