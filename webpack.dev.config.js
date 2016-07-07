var path = require('path');
var webpack = require('webpack');
var ROOT = path.resolve(__dirname, 'src/main/resources/static');
var SRC = path.resolve(ROOT, 'javascript');
var DEST = path.resolve(__dirname, 'build/resources/main/static/javascript');
var HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
    devtool: 'source-map',
    entry: [
        'webpack-hot-middleware/client',
        SRC + '/index.jsx',
        // webpack: 'webpack-dev-server/client?http://0.0.0.0:3000',
        // hot: 'webpack/hot/only-dev-server',
    ],
    resolve: {
        root: [
            path.resolve(ROOT, 'javascript'),
            path.resolve(ROOT, 'css')
        ],
        extensions: ['', '.js', '.jsx']
    },
    output: {
        path: DEST,
        filename: 'bundle.js',
        publicPath: '/javascript/'
    },
    plugins: [
        new webpack.HotModuleReplacementPlugin(),
        new webpack.NoErrorsPlugin(),
        new webpack.DefinePlugin({
            "process.env": {
                NODE_ENV: JSON.stringify('development')
            }
        }),
        // new HtmlWebpackPlugin({
        //     title: 'Boot React',
        //     template: path.join(ROOT, '/react.html')
        // })
    ],
    devServer: {
        hot: true,
        inline: true,
        port: 3000
    },
    module: {
        loaders: [
            {
                test: /\.jsx?$/,  // Notice the regex here. We're matching on js and jsx files.
                loaders: ['babel-loader?presets[]=es2015&presets[]=react'],
                include: SRC
            },

            {test: /\.css$/, loader: 'style-loader!css-loader'},
            {test: /\.less$/, loader: 'style!css!less'},

            // Needed for the css-loader when [bootstrap-webpack](https://github.com/bline/bootstrap-webpack)
            // loads bootstrap's css.
            {test: /\.(woff|woff2)(\?v=\d+\.\d+\.\d+)?$/, loader: 'url?limit=10000&mimetype=application/font-woff'},
            {test: /\.ttf(\?v=\d+\.\d+\.\d+)?$/, loader: 'url?limit=10000&mimetype=application/octet-stream'},
            {test: /\.eot(\?v=\d+\.\d+\.\d+)?$/, loader: 'file'},
            {test: /\.svg(\?v=\d+\.\d+\.\d+)?$/, loader: 'url?limit=10000&mimetype=image/svg+xml'}
        ]
    }
};
