let url = 'http://127.0.0.1:8892';
if(process.env.NODE_ENV != 'development'){
	url = '/api';
}
export default {
	url
}
