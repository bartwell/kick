config.devServer = config.devServer || {};
config.devServer.client = {
  ...(config.devServer.client || {}),
  overlay: false
};
