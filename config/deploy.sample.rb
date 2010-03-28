set :user, 'app'
set :application, 'webling'
set :repository,  'http://github.com/tinkerpop/webling.git'

set :deploy_to, 'place-where-to-deploy'
set :deploy_via, :remote_cache

set :scm, 'git'
set :branch, 'master'
set :scm_username, 'git'
set :scm_auth_cache, false

default_run_options[:pty] = true

role :web, 'your-host-here'
role :app, 'your-host-here'                          
role :db,  'same-as-previous-two'

namespace :deploy do
  task :workers_symlink do
    workers_dir = "#{release_path}/tmp/workers"
    workers_shared_dir = "#{shared_path}/workers"
    
    run "#{try_sudo} rm -rf #{workers_dir} && ln -s #{workers_shared_dir} #{workers_dir}"
  end

  task :build_webling do
    workers_symlink
    run "cd #{current_path} && mvn install -q"
  end

  task :migrate do; end

  task :start do
    build_webling
    run "cd #{release_path} && #{try_sudo} ./webling.sh start 80"
  end
  
  task :stop do
    run "cd #{release_path} && #{try_sudo} ./webling.sh stop"
  end

  task :restart do
    build_webling
    run "cd #{release_path} && #{try_sudo} ./webling.sh restart 80"
  end
end

