import pymysql
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import json

# 유사도 함수
# 인자에 비교할 news no을 넣으면됨!
def Cosine(new_no):
    # DB연결
    conn = pymysql.connect(
        user='admin', 
        passwd='ghdtnswo1!', 
        host='antproject.cjirybd28kms.ap-northeast-2.rds.amazonaws.com', 
        db='AntDB', 
        charset='utf8'
    )
    try:
        curs = conn.cursor(pymysql.cursors.DictCursor)
        sql = "SELECT * FROM `NewsData`"
        curs.execute(sql)
        result = curs.fetchall()
        result = pd.DataFrame(result)

        db_no = []
        cs_per = []
        result_in = []
        result_out = []
        for i in range(len(result)):
            # 코사인유사도 실행
            new_news = result.iloc[new_no-1][1]
            sentence = (result.iloc[i][1], new_news)
            tfidf_vectorizer = TfidfVectorizer()
            # 동일뉴스번호 제외
            if new_no == result.iloc[i][0]:
                continue
            else:
                tfidf_matrix = tfidf_vectorizer.fit_transform(sentence)
                cs_result = cosine_similarity(tfidf_matrix[0:1], tfidf_matrix[1:2])*100
                # 유사도결과값 0% 제외
                if cs_result == [[0.]]:
                    continue
                else:
                    # 유사도 소수 3째자리 반올림
                    result_in = [new_no, result.iloc[i][0], round(float(cs_result),3)]
                    result_out.append(result_in)
            i+1
        # 유사도에 따른 내림차순 정렬
        result_out.sort(key=lambda x:x[2], reverse=True)
        for i in range(len(result_out)):
            db_no.append(result_out[i][1])
            cs_per.append(result_out[i][2])
    finally:
        conn.close
    toJson(new_no, db_no, cs_per)

# JSON형식으로 다시 POST
def toJson(new, db_no, cs_per) :
    data = {}
    data['posts'] = []
    for i in range(len(db_no)):
        data['posts'].append({
            "new_no" : str(new),
            "db_no" : str(db_no[i]),
            "cs_per" : str(cs_per[i]),
            "no" : str(i)
        })
    print(json.dumps(data, indent=4, ensure_ascii=False))
    SaveJson(data)

def SaveJson(data) :
    with open('cosine_json.json', 'w', encoding='utf-8-sig') as json_file:
        json.dump(data, json_file, indent=4, ensure_ascii=False)

# 유사도결과 얼마나 유사한지 문장 눈으로 보려고 만든함수
def Checkdescription(sen1, sen2):
    # DB연결
    conn = pymysql.connect(
        user='admin', 
        passwd='ghdtnswo1!', 
        host='antproject.cjirybd28kms.ap-northeast-2.rds.amazonaws.com', 
        db='AntDB', 
        charset='utf8'
    )
    try:
        curs = conn.cursor(pymysql.cursors.DictCursor)
        sql = "SELECT * FROM `NewsData`"
        curs.execute(sql)
        result = curs.fetchall()
        result = pd.DataFrame(result)


        print(result.iloc[sen1][1])
        print(result.iloc[sen2][1])
    finally:
        conn.close

Cosine(672)
#Checkdescription(672, 691)