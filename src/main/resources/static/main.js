firebase.initializeApp(firebaseConfig);
const messaging = firebase.messaging();

messaging.onMessage((payload) => {
  console.log('Message received. ', payload);
   const notificationTitle = payload.notification.title;
    const notificationOptions = {
      body: payload.notification.body,
      icon: 'resources/static/panda.png',
      actions: [
        {action: 'action1', title: 'Popio sam'},
        {action: 'action2', title: 'Nisam popio'},
             ],
        };
    self.registration.showNotification(notificationTitle, notificationOptions);
});

self.addEventListener('notificationclick', (event) => {
  event.notification.close();
  const cacheName = 'moj-token-cache';
  const tokenURL = '/token';
  caches.open(cacheName).then(cache => {
    cache.match(tokenURL).then(response => {
      if (response) {
        response.text().then(token => {
          console.log('Token dohvaćen iz cache-a:', token);
          const dataToSend = { token: token };
          const jsonData = JSON.stringify(dataToSend);
          let url = 'https://mojasistenttestenvironment-e209f0bbc59e.herokuapp.com/spremiNoReminder';
          if (event.action === 'action1') {
            url = 'https://mojasistenttestenvironment-e209f0bbc59e.herokuapp.com/spremiReminder';
          }
          fetch(url, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: jsonData,
          }).then(response => {
            console.log('Server response:', response);
          }).catch(error => {
            console.error('Error sending data:', error);
          });

        });
      } else {
        console.log('Token nije pronađen u cache-u.');
      }
    });
  });
});