# SDIS1718

## Compilar

Este program pode ser compilado usando o comando javac ou, alternativamente, caso tenha o editor Eclipse, pode importar o projeto para o ficheiro Eclipse e encontrará os ficheiros compilados no diretório bin.

## Correr

Todos os comandos usados para correr o peer ou o client são os mesmos que foram explicitados no enunciado do problema, que ainda assim passaremos a enunciar.

## RMI

Antes de correr um Peer ou um Client deve ser iniciada a RMI, algo que pode ser feito com o comando 
```rmiregistry &```

## Peer
Para correr um Peer deve utilizar o seguinte comando:
```
  java Peer <Protocol_Version> <Server_ID> <Service_Access_Point> <MC_IP_Multicast_Address> <MC_Port> <MDB_IP_Multicast_Address> <MDB_Port> <MDR_IP_Multicast_Address> <MRD_Port>
```
Chamamos especial atenção para o argumento ```<Protocol Version>```  que no nosso sistema apenas pode ser ```1```, caso não se deseje que o peer tenha enhancements ou ```2``` caso se deseje.

## Client
Para correr um Client deve utilizar um dos seguintes comandos:

```
java Client <peer_ap> BACKUP <File_Path> <Replication_Degree>
```
```
java Client <peer_ap> RESTORE <File_Path>
```
```
java Client <peer_ap> DELETE <File_Path>"
```
```
java Client <peer_ap> RECLAIM <space>
```
```
java Client <peer_ap> STATE
```
Todos estes comandos funcionam da maneira indicada no enunciado deste projeto.
