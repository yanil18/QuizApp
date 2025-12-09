var AesUtil = function(keySize, iterationCount) {
  this.keySize = keySize / 32;
  this.iterationCount = iterationCount;
};

AesUtil.prototype.generateKey = function(salt, passPhrase) {
  var key = CryptoJS.PBKDF2(passPhrase,CryptoJS.enc.Hex.parse(salt), { keySize: this.keySize, iterations: this.iterationCount });
  return key;
}

AesUtil.prototype.encrypt = function(plainText,mytext) {
    var iv = CryptoJS.lib.WordArray.random(128/8).toString(CryptoJS.enc.Hex);
    var salt = CryptoJS.lib.WordArray.random(128/8).toString(CryptoJS.enc.Hex);
    
    var key = this.generateKey(salt,mytext);
    var encrypted = CryptoJS.AES.encrypt( plainText,key,{ iv: CryptoJS.enc.Hex.parse(iv) });
    
    return btoa((iv + "::" + salt + "::" + encrypted.ciphertext.toString(CryptoJS.enc.Base64)));
}

AesUtil.prototype.decrypt = function(salt, iv, passPhrase, cipherText) {
  var key = this.generateKey(salt, passPhrase);
  var cipherParams = CryptoJS.lib.CipherParams.create({ciphertext: CryptoJS.enc.Base64.parse(cipherText) });  
  var decrypted = CryptoJS.AES.decrypt(cipherParams, key,{ iv: CryptoJS.enc.Hex.parse(iv) });
  return decrypted.toString(CryptoJS.enc.Utf8);
}

AesUtil.prototype.decrypt1 = function(passPhrase, cipherText) {
	var temp = cipherText.split("::");
	var salt = temp[0];
	var iv = temp[1]
	cipherText = temp[2];
	var key = this.generateKey(salt, passPhrase);
	var cipherParams = CryptoJS.lib.CipherParams.create({ ciphertext: CryptoJS.enc.Base64.parse(cipherText) });
	var decrypted = CryptoJS.AES.decrypt(cipherParams, key, { iv: CryptoJS.enc.Hex.parse(iv) });
	decrypted.toString(CryptoJS.enc.Utf8);
	return decrypted.toString(CryptoJS.enc.Utf8);
}

function decodewithtime(entxt)
{
//	entxt="YWE2NzgwZjQxOTgzYmZmMGZhYjUxYTkwOWE1YjI2YWQ6OjE0MTE4MTRhNDMyOWJhZjJjNTAxY2IxZWViODA5YWIwOjpzcG03QWp2WVIvQjBvZCtOVU8xdlpoMTZMdndZSzNOUnJibWRJR2NkMStNPQ==";
	var aesUtil = new AesUtil(128, 1000);
	var enc = atob(entxt).split("::");
	var decrypted = aesUtil.decrypt(enc[1],enc[0],"CD0M7ZMGXIphBqB3USmt13h7pxHtaMgL",enc[2]); 
	var time = decrypted.split("::")[1];
	var decrypttext = decrypted.split("::")[0];
    const currentTime = Math.floor(Date.now() / 1000); 
    const timeDiff = currentTime - time;
//console.log(timeDiff)
    if (timeDiff > 20){
        return null; 
    }
	return decrypttext;
}