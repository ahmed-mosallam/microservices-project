def buildAll = false
def affectedModules = []
def goal = "install"
def affectedK8sFiles=[]
def parentModule="microservices-project"
pipeline {
	
	agent any
	//disable concurrent build to avoid race conditions and to save resources
	options {
        disableConcurrentBuilds()
    }

	//load tools - these should be configured in jenkins global tool configuration
	 tools { 
        maven 'Default'
    }
	stages {
	    stage('checkout'){
            steps{
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[credentialsId: '136b6e01-6b99-4ae5-9dda-b05891a91253', url: 'https://github.com/ahmed-mosallam/microservices-project']])
                // dir('microservices-project/product-service') {
                //   bat('mvn clean install -DskipTests')
                // }
                //sh 'mvn clean install -DskipTests'
            }
        }
		//this stage will get all the files that were modified
		stage("get diff") {
			steps {
				script {
					def changes = []					
					if(currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause').size() > 0) { //check if triggered via User 'Build Now'
						echo "User Trigger"						
						buildAll = true
					} else { //defaults to Push Trigger
						echo "Push Trigger"
						//get changes via changelogs so we can know which module should be built
						def changeLogSets = currentBuild.changeSets
						
						for (int i = 0; i < changeLogSets.size(); i++) {
							def entries = changeLogSets[i].items
							
							for (int j = 0; j < entries.length; j++) {
								def entry = entries[j]
								
								def files = new ArrayList(entry.affectedFiles)
								
								for (int k = 0; k < files.size(); k++) {
									def file = files[k]
									echo "file path : ${file.path}"
									changes.add(file.path)
								}
							}	
						}						
					}
					//iterate through changes
					changes.each {c -> 				    
						if(c.indexOf("Jenkinsfile")==-1){
							if(c.indexOf("k8s")==-1 ){
							  def changeSplits = c.split("/",1)
							  affectedModules.add(changeSplits[1].substring(0,c.indexOf("/")))
							}else{
							  def changeSplits = c.split("/",1)
							  affectedK8sFiles.add(changeSplits[1])
						    }
                        } 						
					}
					
					for(int i=0; i< affectedModules.size(); i++ ){
					   echo "affected module ${i+1} : ${affectedModules[i]}"
					}
					
					for(int i=0; i< affectedK8sFiles.size(); i++ ){
					   echo "affected k8s file ${i+1} : ${affectedK8sFiles[i]}"
					}
					
				}
			}
		}
		//this stage will build all if the flag buildAll = true
		stage("build all modules") {
			when {
				expression {
					return buildAll 
				}
			}
			steps {
				script {
					goal = install		
					// -T 5 means we can build modules in parallel using 5 Threads, we can scale this
					if (isUnix()) {
						sh "mvn clean ${goal} -B -DskipTests -Pbuild -T 5" 
					} else {
						bat "mvn clean ${goal} -B -DskipTests -Pbuild -T 5"
					}
				}

			}
		}
		//this stage will build the affected modules only if affectedModules.size() > 0
		stage("build affected modules & docker images & push to docker hup") {
			when {
				expression {
					return affectedModules.size() > 0
				}
			}

			steps {
				script {					
					goal = "install"		
					// -T 5 means we can build modules in parallel using 5 Threads, we can scale this
					for(int i=0; i< affectedModules.size(); i++ ){
					    dir('${parentModule}/${affectedModules[i]}'){
							if (isUnix()) {
								sh "mvn clean ${goal} -DskipTests"
								sh('docker build -t ahmedmosallam/${affectedModules[i]}:latest .')
								withCredentials([string(credentialsId: 'dockerhup-pwd', variable: 'dockerhup-pwd')]) {
                                   sh 'docker login -u ahmedmosallam -p ${dockerhup-pwd}'
                                }
								sh 'docker push ahmedmosallam/${affectedModules[i]}:latest'
								
							} else {
								bat "mvn clean ${goal} -DskipTests"	
								bat('docker build -t ahmedmosallam/${affectedModules[i]}:latest .')
								withCredentials([string(credentialsId: 'dockerhup-pwd', variable: 'dockerhup-pwd')]) {
                                   bat 'docker login -u ahmedmosallam -p ${dockerhup-pwd}'
                                }
								sh 'docker push ahmedmosallam/${affectedModules[i]}:latest'
							}
							
							
						}
					}			
				}

			}
		}
        
				
		stage("apply k8s files"){
		  steps{
		    script{
			   for(int i=0; i< affectedK8sFiles.size(); i++ ){
				def changedK8sFileNameSplits = affectedK8sFiles[i].split("/")
				def changedK8sFileName = changedK8sFileNamesSplits[changedK8sFileNamesSplits.length-1]
				echo 'changed k8s file name ${changedK8sFileName}'
				dir('${parentModule}/${affectedK8sFiles[i]}'){
					if (isUnix()) {
						sh "kubectl apply -f ${changedK8sFileName}"
					} else {
						bat "kubectl apply -f ${changedK8sFileName}"
					}
				}
			  }
			}		  
		  }
		}
		
	}	
}