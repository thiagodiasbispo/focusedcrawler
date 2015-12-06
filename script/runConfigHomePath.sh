cd $1
echo "HOME $1" > conf/home.cfg
echo 'DATA_DIR ${HOME}/data' >> conf/home.cfg
echo 'SCRIPT_DIR ${HOME}/script' >> conf/home.cfg
echo 'CONFIG_DIR ${HOME}/conf' >> conf/home.cfg
