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
