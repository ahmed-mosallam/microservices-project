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
							  def changeSplits = c.split("/")
							  affectedModules.add(changeSplits[1])
							}else{
							  def changeSplits = c.split("/",2)
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
					    dir(parentModule+'/'+affectedModules[i]){
					        def tag = '1.1'
					        def imageNameTag = 'ahmedmosallam/'+affectedModules[i]+':'+tag
							if (isUnix()) {
								sh "mvn clean ${goal} -DskipTests"
								sh "docker build -t ${imageNameTag} ."
								withCredentials([string(credentialsId: 'dockerhub-pwd', variable: 'dockerhubpwd')]) {
                                   sh "docker login -u ahmedmosallam -p ${dockerhubpwd}"
                                   sh "docker push ${imageNameTag}"
                                }								
							} else {
								bat "mvn clean ${goal} -DskipTests"	
								bat "docker build -t ${imageNameTag} ."
								withCredentials([string(credentialsId: 'dockerhub-pwd', variable: 'dockerhubpwd')]) {
                                   bat "docker login -u ahmedmosallam -p ${dockerhubpwd}"
                                   bat "docker push ${imageNameTag}"
                                }
								
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
				def changedK8sFileName = changedK8sFileNameSplits[changedK8sFileNameSplits.length-1]
				echo "changed k8s file name ${changedK8sFileName}"
				def affectedFilePath =affectedK8sFiles[i].substring(0,affectedK8sFiles[i].lastIndexOf("/")) 
			    echo "affected k8s file path ${affectedFilePath}"
				def workDir=parentModule+'/'+affectedFilePath
				echo "working directory ${workDir}"
				dir(workDir){
					if (isUnix()) {
						sh "kubectl apply -f ${changedK8sFileName} --context minikube"
					} else {
					    kubeconfig(credentialsId: 'cd478dd4-44a1-4d69-800e-a98a66bcc5fb', serverUrl: 'https://192.168.59.100:8443') {
                           bat "kubectl apply -f ${changedK8sFileName}"
                        }
						
					}
				}
			  }
			}		  
		  }
		}
		
	}	
}