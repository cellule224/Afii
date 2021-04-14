// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();



/*****************************************************************
*                                                                *
*                     TRANSACTION                                *  
*                                                                *
*                                                                *
*****************************************************************/   
   
exports.makeTransaction = functions.region('europe-west1').database.ref('/Transactions/{tr_id}')
  .onCreate(async (snap, context) => {

  const transaction = snap.val();
  const transactionId = transaction.tr_id;
  const receiverUid = transaction.tr_receiver_id;
  const receiverPhone = transaction.tr_receiver_phone_num;
  const senderUid = transaction.tr_sender_id;
  const senderPhone = transaction.tr_sender_phone_num;
  const amountReceived = parseInt(transaction.tr_amount);
  const transactionTime = transaction.tr_time;

  //Sender snapshot
  let senderSnap;
  //Receiver snapshot
  let receiverSnap;

  //Get sender's balance
  const senderBeforeBalance = admin.database()
    .ref(`/users/${senderUid}/user_data/u_balance`).once('value').then(senderSnap);

  //Get receiver's balance
  const receiverBeforeBalance = admin.database()
    .ref(`/users/${receiverUid}/user_data/u_balance`).once('value').then(receiverSnap);

  const balances = await Promise.all([senderBeforeBalance, receiverBeforeBalance]);
  senderSnap = balances[0];
  receiverSnap = balances[1];

  var bSenderBalance = senderSnap.val();
  var bReceiverBalance = receiverSnap.val();

  console.log("Sender balance before is: ".concat(bSenderBalance));
  console.log("Receiver balance before is: ".concat(bReceiverBalance));

//Transaction math method
  console.log("...Transactioning " + amountReceived);
  var aSenderBalance = bSenderBalance - amountReceived;
  var aReceiverBalance = parseInt(bReceiverBalance) + amountReceived;
  var aSenderBalAfterfees = aSenderBalance - ((amountReceived / 100) * 2); //2% tr fees
  
  console.log("Sender balance After is: " + aSenderBalance);
  console.log("Receiver balance After is: " + aReceiverBalance);
  console.log("Sender Final Balance: " + aSenderBalAfterfees);
    
//Check if balance is ok, if not return.
if (aSenderBalAfterfees < 0) {
  console.log("Sender balance + fees not enough. Tr Canceled!");
  return;
}

//Check if sender is receiver, if so return.
if (senderUid == receiverUid) {
	console.log("Sender and Receiver is the same id. Tr Canceled!");
	return;
}

//If transaction okayed, update users' respective balances in db
admin.database().ref(`/users/${senderUid}/user_data/u_balance`).set(aSenderBalAfterfees.toString());
admin.database().ref(`/users/${receiverUid}/user_data/u_balance`).set(aReceiverBalance.toString());

//And add it to their transaction history in db
const transactionHistory = {
  tr_id: transactionId,
  tr_sender_phone_num: senderPhone,
  tr_receiver_phone_num: receiverPhone,
  tr_receiver_id: receiverUid,
  tr_amount: amountReceived.toString(),
  tr_time: transactionTime
};

admin.database().ref(`/users/${receiverUid}/user_trans_history/${transactionId}/`).set(transactionHistory);
admin.database().ref(`/users/${senderUid}/user_trans_history/${transactionId}/`).set(transactionHistory);

});




/*****************************************************************
*                                                                *
*                        NOTIFICATION                            *
*                                                                *
*                                                                *
*****************************************************************/   

exports.transactionNotification = functions.region('europe-west1').database.ref(`/users/{user_id}/user_trans_history/{tr_id}`)
   .onCreate(async (snap, context) => {    

    const transaction = snap.val();
    const receiverUid = transaction.tr_receiver_id;
    const senderPhone = transaction.tr_sender_phone_num;
    const amountReceived = transaction.tr_amount;
    const tempReceiverRef = snap.ref.parent;
    const temp = tempReceiverRef.parent;

    //If it's the sender's node do not send notification
    if (new String(temp).valueOf() !== new String(admin.database().ref(`/users/${receiverUid}`)).valueOf()) {
      console.log("No need to send notification to sender. Notification Canceled");
      console.log("This: " + temp + " is the same..");
      console.log("..as this: " + admin.database().ref(`/users/${receiverUid}`));
      return;
    }

// Get the list of device notification tokens.
    const getDeviceTokensPromise = admin.database()
        .ref(`/users/${receiverUid}/user_tokens`).once('value');

    // Get the follower profile.
    const getSenderProfilePromise = admin.auth().getUser(receiverUid);

    // The snapshot to the user's tokens.
    let tokensSnapshot;

    // The array containing all the user's tokens.
    let tokens;

    const results = await Promise.all([getDeviceTokensPromise, getSenderProfilePromise]);
    tokensSnapshot = results[0];
    const sender = results[1];

    // Check if there are any device tokens.
    if (!tokensSnapshot.hasChildren()) {
      return console.log('There are no notification tokens to send to.');
    }
    console.log('There are', tokensSnapshot.numChildren(), 'tokens to send notifications to.');


  // Notification details.
  const payload = {
    notification: {
      title: 'Paiement reçu!',
      body: `${senderPhone} vous a envoyer `.concat(amountReceived).concat("USD")
    }
  };


// Listing all tokens as an array.
  tokens = Object.keys(tokensSnapshot.val());
  // Send notifications to all tokens.
  const response = await admin.messaging().sendToDevice(tokens, payload);
  // For each message check if there was an error.
  const tokensToRemove = [];
  response.results.forEach((result, index) => {
    const error = result.error;
    if (error) {
      console.error('Failure sending notification to', tokens[index], error);
      // Cleanup the tokens who are not registered anymore.
      if (error.code === 'messaging/invalid-registration-token' ||
          error.code === 'messaging/registration-token-not-registered') {
        tokensToRemove.push(tokensSnapshot.ref.child(tokens[index]).remove());
      }
    }
  });
  return Promise.all(tokensToRemove);
});



/*****************************************************************
*                                                                *
*                        VALIDATE PIN                            *
*                                                                *
*                                                                *
*****************************************************************/ 

exports.checkPin = functions.https.onCall((data, context) => {
	
	// Checking that the user is authenticated.
  if (!context.auth) {
    // Throwing an HttpsError so that the client gets the error details.
    throw new functions.https.HttpsError('failed-precondition', 'Vous devez vous connectez pour ' +
        'effectuer cette manipulation.');
  }

  const requesterId = context.auth.uid;
  const pinAuthorId = data.pinAuthorId;
  const enteredPin = data.enteredPin;

  //get pin Author ID




  // [START returnData]
  // returning result.
  return {
    result: correct,
  };
  // [END returnData]

});