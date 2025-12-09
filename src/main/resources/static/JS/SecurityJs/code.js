// Encryption process
function chkV(text){
return	HybridEncryption(text);
/*var encryptedBase64Key1 = $("meta[name='abcd']").attr("content");
var encryptedBase64Key2 = $("meta[name='jklm']").attr("content");
var encryptedBase64Key3 = $("meta[name='mmnn']").attr("content");
var encryptedBase64Key = encryptedBase64Key1+encryptedBase64Key2+encryptedBase64Key3;


var parsedBase64Key = CryptoJS.enc.Base64.parse(encryptedBase64Key);
var encryptedData = null;
var plaintText = text;
// console.log( “plaintText = “ + plaintText );
// this is Base64-encoded encrypted data
encryptedData = CryptoJS.AES.encrypt(plaintText, parsedBase64Key, {
mode: CryptoJS.mode.ECB,
padding: CryptoJS.pad.Pkcs7
});
//console.log( "encryptedData = " + encryptedData );
return encryptedData;*/
}

function chkA(text){
var encryptedBase64Key1 = $("meta[name='abcd']").attr("content");
var encryptedBase64Key2 = $("meta[name='jklm']").attr("content");
var encryptedBase64Key3 = $("meta[name='mmnn']").attr("content");
var encryptedBase64Key = encryptedBase64Key1+encryptedBase64Key2+encryptedBase64Key3;


var parsedBase64Key = CryptoJS.enc.Base64.parse(encryptedBase64Key);
var encryptedData = null;
var plaintText = text;
// console.log( “plaintText = “ + plaintText );
// this is Base64-encoded encrypted data
encryptedData = CryptoJS.AES.encrypt(plaintText, parsedBase64Key, {
mode: CryptoJS.mode.ECB,
padding: CryptoJS.pad.Pkcs7
});
//console.log( "encryptedData = " + encryptedData );
return encryptedData;
}

// Decryption process
function setV(text){
var encryptedCipherText = text ; // or encryptedData;
var encryptedBase64Key1 = $("meta[name='abcd']").attr("content");
var encryptedBase64Key2 = $("meta[name='jklm']").attr("content");
var encryptedBase64Key3 = $("meta[name='mmnn']").attr("content");
var encryptedBase64Key = encryptedBase64Key1+encryptedBase64Key2+encryptedBase64Key3;
var parsedBase64Key = CryptoJS.enc.Base64.parse(encryptedBase64Key);
var decryptedData = CryptoJS.AES.decrypt( encryptedCipherText, parsedBase64Key, {
mode: CryptoJS.mode.ECB,
padding: CryptoJS.pad.Pkcs7
} );
// console.log( “DecryptedData = “ + decryptedData );
// this is the decrypted data as a string
var decryptedText = decryptedData.toString( CryptoJS.enc.Utf8 );
//console.log( "DecryptedText = " + decryptedText );
return decryptedText;
}

//// random string ///
function makeid(length) {
    let result = '';
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    const charactersLength = characters.length;
    let counter = 0;
    while (counter < length) {
      result += characters.charAt(Math.floor(Math.random() * charactersLength));
      counter += 1;
    }
    return result;
}


