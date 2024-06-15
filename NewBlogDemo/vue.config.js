const { defineConfig } = require('@vue/cli-service')

const Timestamp = new Date().getTime();
const MiniCssExtractPlugin = require('mini-css-extract-plugin');



module.exports = defineConfig({
  publicPath: '/xiba-shop/', // 子域名项目必备绝对路径 静态文件 适配history
  transpileDependencies: true,
  devServer: {
    client: {
      overlay: false
    },
    // 自己加的跨域尝试 可以用在开发环境 生产环境 nginx反代
    proxy: 'http://localhost:8004',//仅限开发环境
    host: '0.0.0.0',//仅限开发环境
    allowedHosts: [
      'yjzblog.site', // 允许访问的域名地址，即花生壳内网穿透的地址
      '.yjzblog.site'   // .是二级域名的通配符   
    ], //仅限开发环境
    port: 8080, // 更改为你希望的端口//仅限开发环境
  },
  configureWebpack: {
    output: {
      // 为JS文件名添加时间戳
      filename: `[name].${Timestamp}.js`,
      chunkFilename: `[name].${Timestamp}.js`
    },
    module: {
      rules: [
        {
          test: /\.webp$/,
          type: 'asset/resource',
          generator: {
            filename: 'img/[name][ext]' // 对于.webp文件，去除哈希
          }
        }
      ]
    },
    plugins: [
      // 使用MiniCssExtractPlugin来指定CSS文件名，包括时间戳
      new MiniCssExtractPlugin({
        filename: `css/[name].${Timestamp}.css`,
        chunkFilename: `css/[name].${Timestamp}.css`,
      }),

    ],
  },
})
