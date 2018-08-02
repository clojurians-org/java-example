{ fetchFromGitHub, maven, jdk, makeWrapper, stdenv, ... }:
stdenv.mkDerivation rec {
  name = "first-rest-${version}" ;
  version = "0.0.1";
  rev = "9bb2448803c5befe3dcf8e77ac02876784467574" ;

  src = fetchFromGitHub {
    owner = "clojurians-org" ;
    repo = "java-example" ; 
    rev = rev ;
    sha256 = "017jmd00g1g06agdmhjh6csfxh7p6697m5li0pbxyn2lck4y5lf5" ;
  } ;

  fetchedMavenDeps = stdenv.mkDerivation {
    name = "${name}-maven-deps";
    inherit src nativeBuildInputs;
    buildPhase = ''
      cd first-rest
      while timeout --kill-after=21m 20m mvn package -Dmaven.repo.local=$out/.m2; [ $? = 124 ]; do
        echo "maven hangs while downloading :("
      done
    '';
    installPhase = ''find $out/.m2 -type f \! -regex '.+\(pom\|jar\|xml\|sha1\)' -delete''; # delete files with lastModified timestamps inside
    outputHashAlgo = "sha256";
    outputHashMode = "recursive";
    outputHash = "030xfmp01krzgci49m0asnrwvwi57y0sdaajxgyjbx0ag9y3pih7" ;
  };

  nativeBuildInputs = [ maven ];
  buildInputs = [ makeWrapper ];
  buildPhase = ''
      cd first-rest
      mvn package --offline -Dmaven.repo.local=$(cp -dpR ${fetchedMavenDeps}/.m2 ./ && chmod +w -R .m2 && pwd)/.m2
  '';
  meta = with stdenv.lib; {
    homepage = "https://github.com/clojurians-org/java-example" ;
    description = "first-rest" ;
    license = licenses.asl20;
    platforms = platforms.unix;
  };

  installPhase = ''
    mkdir -p $out/bin
    mkdir -p $out/share/java
    mkdir -p $out/tmp
    mv target/first-rest-0.0.1-SNAPSHOT.jar $out/share/java/$name.jar
    # makeWrapper ${jdk}/bin/java $out/bin/start-first-rest.sh --add-flags "-jar $out/share/java/$name.jar" --suffix PATH : ${stdenv.lib.makeBinPath [ jdk ]}
    cd $out/share/java
    ${jdk}/bin/jar -xvf $name.jar
    rm $name.jar
    makeWrapper ${jdk}/bin/java $out/bin/start-first-rest.sh --add-flags "-cp $out/share/java org.springframework.boot.loader.JarLauncher" --suffix PATH : ${stdenv.lib.makeBinPath [ jdk ]}
  '';
}

