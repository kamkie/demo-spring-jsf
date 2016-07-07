var webpack = require('webpack');
// var WebpackDevServer = require('webpack-dev-server');
var config = require('./webpack.config');

// new WebpackDevServer(webpack(config), {
//     publicPath: config.output.publicPath,
//     hot: true,
//     historyApiFallback: false,
//     proxy: {
//         "*": "http://localhost:8080"
//     },
//     quiet: false,
//     noInfo: false,
//     lazy: false,
//     filename: "bundle.js",
//     watchOptions: {
//         aggregateTimeout: 300,
//         poll: 1000
//     },
//     headers: {"X-Frame-Options": "ALL"},
//     stats: {colors: true}
// }).listen(3000, 'localhost', function (err, result) {
//     if (err) {
//         console.log(err);
//     }
//
//     console.log('Listening at localhost:3000');
// });

var path = require('path');
var express = require('express');
var history = require('connect-history-api-fallback');
var proxy = require('http-proxy-middleware');
var webpackDevMiddleware = require('webpack-dev-middleware');
var webpackHotMiddleware = require('webpack-hot-middleware');

var app = express();
var compiler = webpack(config);

app.use(history());

app.use(webpackDevMiddleware(compiler, {
    noInfo: true,
    publicPath: config.output.publicPath,
    stats: {colors: true}
}));
app.use(webpackHotMiddleware(compiler));

var options = {
    target: 'http://localhost:8080', // target host
    changeOrigin: true,               // needed for virtual hosted sites
    pathRewrite: {
        '^/proxy': '/'     // rewrite path
    },
    proxyTable: {
        // when request.headers.host == 'dev.localhost:3000',
        // override target 'http://www.example.org' to 'http://localhost:8000'
        'dev.localhost:3000': 'http://localhost:8000'
    }
};
app.use('/proxy', proxy(options));
// app.use(proxy('/proxy', {target: 'http://localhost:8080', changeOrigin: true}));


app.listen(3000, 'localhost', function (err) {
    if (err) {
        console.log(err);
        return;
    }

    console.log('Listening at http://localhost:3000');
});
