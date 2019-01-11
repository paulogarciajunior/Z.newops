#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Dec  4 14:45:45 2018

@author: paulo
"""


import requests
import json


host  = "http://34.201.213.20"
port  = ":8180"
server= '/kie-server'


def executarRules():

    path  = '/services/rest/server/containers/instances/'
    app   = 'Regulacao_1.0.0'
    

    url = host + port + server + path + app

    contexto = 'com.newops.regulacao.solicitacao'

    #Conteudo original
    conteudo = '''
        <batch-execution lookup="defaultStatelessKieSession">
          <insert out-identifier="solicitacao" return-object="false" entry-point="DEFAULT">
            <com.newops.regulacao.solicitacao>
                <nome>PAULO</nome>
                <idade>49</idade>
                <autorizado>true</autorizado>
            </com.newops.regulacao.solicitacao>
            <com.newops.regulacao.evento>
                <codigo>1010</codigo>
                <idademin>1</idademin>
                <idademax>10</idademax>
            </com.newops.regulacao.evento>
          </insert>
          <fire-all-rules/>
          <get-objects out-identifier="output"/>
        </batch-execution>'''

  
    headers = {'Content-type': 'application/xml','X-KIE-ContentType': 'xstream'}
    #headers = {'Content-type': 'application/json'}
    response = requests.post(url, data=conteudo, headers=headers,auth=('admin', 'admin'))
    print (response.text)
    print (response.status_code)

def executarProcesso():
   
    app   = 'Regulacao_1.0.0'
    processo = 'Regulacao.proc_validar'
    path  = ('/services/rest/server/containers/%s/processes/%s/instances' % (app,processo))
    

    conteudo = {
              "solicitacao": {
                "com.newops.regulacao.solicitacao": {
                  "nome": "PAULO GARCIA JUNIOR",
                  "idade":2,
                  "autorizado":True,
                  "id":1
                }
              },
              "evento": {
                "com.newops.regulacao.evento": {
                  "codigo": 10014,
                  "descricao":"PET-SCAN",
                  "idademin":10,
                  "idademax":50
                }
              },
              "solicitante": {
                "com.newops.regulacao.solicitante": {
                  "codigo": "304015",
                  "especialidade":"IMAGEM",
                  "nome":"LABORATORIO SAO CAMILO"
                }    
              }
    }
    
    url = host + port + server + path

    headers = {'Content-type': 'application/json'}
    response = requests.post(url, json=conteudo, headers=headers,auth=('admin', 'admin'))
    print (response.text)
    print (response.status_code)
    return response.text
    
    
def getVariaveis(instancia):

    app   = 'Regulacao_1.0.0'
   
    path = ('/services/rest/server/containers/%s/processes/instances/%s/variable/solicitacao' % (app,instancia))
    
    
    print (path)
    url = host + port + server + path

    headers = {'Content-type': 'application/json'}
    response = requests.get(url, headers=headers,auth=('admin', 'admin'))
    print (response.text)
    print (response.status_code)
    
    
#executarRules()
instancia = executarProcesso()
getVariaveis(instancia)