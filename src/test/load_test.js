import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 1, // количество виртуальных пользователей
    duration: '1m',
};


const locations = [
    { city: 'New York', address: '123 Main St, New York, NY 10001' },
    { city: 'Los Angeles', address: '456 Elm St, Los Angeles, CA 90001' },
    { city: 'Chicago', address: '789 Oak St, Chicago, IL 60601' },
    { city: 'Houston', address: '101 Pine St, Houston, TX 77001' },
    { city: 'Phoenix', address: '202 Maple St, Phoenix, AZ 85001' },
];


export default function () {
    const username = `user_${Math.floor(Math.random() * 100000)}`;
    const email = `${username}@example.com`;
    const password = `password{Math.floor(Math.random() * 100000)}`;
    const location = locations[Math.floor(Math.random() * locations.length)];

    const res = http.post('http://localhost:8080/api/auth/signup', JSON.stringify({
        username: username,
        email: email,
        password: password,
        location: location.city + location.address
    }), {
        headers: { 'Content-Type': 'application/json' },
    });

    check(res, {
        'is status 200': (r) => r.status === 200,
    });
    console.log('Response time was ' + String(res.timings.duration) + ' ms');

    sleep(1);
}
