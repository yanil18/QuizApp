//-------------------      Code STARTS BY ABHI     ------------------//
//------------------- FOR GLOBAL HYBRID ENCRYPTION ------------------//
var aesKey=null;
var publicKeyPEM=null;
var token =$('input[name="_csrf"]').val();
// fetch('/Gujarat/keys',{
// method: 'POST',
// headers: {
//     'Content-Type': 'application/json',
//    'X-CSRF-Token':token
// }
// })
// .then(response => response.json())
// .then(data => {
//      aesKey = data.HarshKey;
//      publicKeyPEM = data.AbhiKey;
// })
// .catch(error => console.error('Error fetching keys:', error));
let cp = $('#context_path').val();

async function fetchData() {
  try {

      const response = await fetch(cp+'/keys', {

          method: 'POST',
          headers: {
              'Content-Type': 'application/json',
              'X-CSRF-Token': token
          }
      });

     // console.log(response)

      if (!response.ok) {
          throw new Error('Failed to fetch data');
      }

      const data = await response.json();
      
       
      aesKey = data.HarshKey;
      publicKeyPEM = data.AbhiKey;
     
      // console.log(aesKey);
       HybridEncryption(aesKey);
  } catch (error) {
      console.error('Error fetching keys:', error);
  }
}

fetchData();
function HybridEncryption(normaldata) {
  // console.log("aesKey   "+normaldata)
    // Add the timestamp to the data
    // const dataWithTimestamp = getPasswordWithTimestamp(normaldata);

    //-------------------     AES ENCRYPTION BEGINS    ------------------//
    var aesUtil = new AesUtil(256, 1000);
    var aes_encrypted_data = aesUtil.encrypt(normaldata, atob(aesKey));
    //-------------------     AES ENCRYPTION ENDS      ------------------//  
    //-------------------     RSA ENCRYPTION BEGINS    ------------------//
    var publicKey = forge.pki.publicKeyFromPem(atob(publicKeyPEM));
    var encryptedChunks = [];
    for (var i = 0; i < aes_encrypted_data.length; i += 380) {
        var chunk = aes_encrypted_data.slice(i, i + 380);
        var encryptedChunk = publicKey.encrypt(chunk, "RSA-OAEP", {
            md: forge.md.sha256.create(),
        });
        encryptedChunks.push(encryptedChunk);
    }
    var Hybrid_Encryption_Data = encryptedChunks.map(function(chunk) {
        return forge.util.encode64(chunk);
    });
    return Hybrid_Encryption_Data.join('');
    //-------------------     RSA ENCRYPTION ENDS    ------------------//
}


function HybridEncryption_WithTime(normaldata) {
    var timestamp;
    $.ajax({
      url: 'getservertime',
      type: 'GET',
      async: false, 
      success: function(data) {
        timestamp = data;
      },
      error: function(jqXHR, textStatus, errorThrown) {
        console.error('AJAX error:', errorThrown);
      }
    });
  
    if (typeof timestamp === 'string') {
      var encdata = HybridEncryption(normaldata + '::' + timestamp);
      return encdata;
    } else {
      console.error('Invalid timestamp format');
      return null; 
    }
  }


