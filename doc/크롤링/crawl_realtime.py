import requests
from bs4 import BeautifulSoup
import re
import json
from datetime import datetime
import pymysql
import pandas as pd

# 1시간 주기 크롤링 함수(회사이름)
def Crawler(company):
    s_year = datetime.now().year
    s_month = datetime.now().month
    s_day = datetime.now().day
    yesterday = DayChange(s_year, s_month, s_day)
    today = MonthChange(s_year, s_month, s_day)
    y_from = yesterday.replace(".", "")
    t_to = today.replace(".", "")

    title_text = []
    link_text = []
    contents_text = []
    date_text = []
    no_text = []
    com_text = []
    idx = "1"
    go = True
    newsNo = DB()

    while go :
        url = "https://search.naver.com/search.naver?where=news&sm=tab_pge&query="+company+"&sort=1&photo=0&field=0&pd=3&ds="+yesterday+"&de="+today+"&mynews=0&office_type=0&office_section_code=0&news_office_checked=&nso=so:dd,p:from"+y_from+"to"+t_to+",a:all&start="+idx
        req = requests.get(url, headers={'User-Agent' : 'Mozilla/5.0'})
        soup = BeautifulSoup(req.text, 'html.parser')

        # 날짜 받아오기(최신뉴스만 받아오기 수정완료)
        spans = soup.find_all('span', 'info')
        for span in spans:
            p1 = re.compile(r'TOP$')
            p2 = re.compile(r'단$')
            p3 = re.compile(r'분 전$')
            m1 = p1.search(span.text)
            m2 = p2.search(span.text)
            m3 = p3.search(span.text)
            if m3:
                year = datetime.now().year
                month = datetime.now().month
                day = datetime.now().day
                hour = datetime.now().hour
                minute = datetime.now().minute
                date_text.append(MinuteChange(year, month, day, hour, minute, span.text))
                continue
            elif m1 or m2:
                continue
            else :
                go = False
                break

        # 제목, url 받아오기
        atags = soup.find_all('a', 'news_tit')
        for atag in atags:
            if len(title_text) < len(date_text) :
                title = atag.get('title')
                title_text.append(title)
                link_text.append(atag['href'])
            else :
                break

        # 본문 받아오기
        contents_lists = soup.find_all('a', 'api_txt_lines dsc_txt_wrap')
        for contents_list in contents_lists:
            if len(contents_text) < len(date_text) :
                contents_text.append(contents_list.text)
                no_text.append(newsNo)
                com_text.append(company)
                newsNo += 1
            else :
                break

        idx = str(int(idx) + 10)
        
        
    title_text.reverse()
    link_text.reverse()
    contents_text.reverse()
    date_text.reverse()
    toJson(no_text, title_text, link_text, contents_text, date_text, com_text)

# JSON 형식 저장
def toJson(no, title, link, contents, date, com) :
    data = {}
    data['posts'] = []
    for i in range(len(date)):
        data['posts'].append({
            "newsNo" : no[i],
            "title" : title[i],
            "description" : contents[i],
            "newsDate" : date[i],
            "url" : link[i],
            "company" : com[i]
        })
    print(json.dumps(data, indent=4, ensure_ascii=False))
    #SaveJson(data)

# JSON 파일 저장
def SaveJson(data) :
    with open('crawl_json.json', 'w', encoding='utf-8-sig') as json_file:
        json.dump(data, json_file, indent=4, ensure_ascii=False)

# -분 전 날짜수정
def MinuteChange(year, month, day, hour, minute, span) :
    target = span.split('분')[0]
    minute -= int(target)
    if minute <= 0:
        hour -= 1
    if hour <= 0:
        day -= 1
        hour = 24-hour
    return MonthChange(year,month,day)

# s_date 날짜수정
def DayChange(year, month, day) :
    day -= 1
    return MonthChange(year, month, day)

# 날짜 공통수정사항
def MonthChange(year, month, day):
    if day <= 0:
        month -= 1
        if month <= 0:
            year -= 1
            month = 12

        if(month in [1,3,5,7,8,10,12]):
            day = 31 - day
        elif(month in [4,6,9,11]):
            day = 30 - day
        elif(month in [2]):
            if(((year%4==0) and (year%100!=0)) or (year%400==0)):
                day = 29 - day
            else:
                day = 28 - day
    if day < 10:
        day = "0"+str(day)
    else:
        day = str(day)
    if month < 10:
        month = "0"+str(month)
    else:
        month = str(month)
    newdate = str(year) + "." + month + "." + day + "."
    return newdate

# DB에서 순번(newsNo) 받아와서 그다음 숫자로 입력
def DB():
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
        newsNo = len(result.index)
    finally:
        conn.close()
    return newsNo

Crawler("네이버")