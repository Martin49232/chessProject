import sqlite3
import json
import chess.pgn
import io 

DB_NAME = "openings.db"


def pgn_to_san_mainline(pgn_string):
    pgn_io = io.StringIO((pgn_string.strip()))
    game = chess.pgn.read_game(pgn_io)

    board = game.board()

    san_moves = []

    for move in game.mainline_moves():
        san_moves.append(board.san(move))
        board.push(move)
    
    return " ".join(san_moves)




def pgn_to_fen_list(pgn_string):
    board = chess.Board()
    san_mainline = pgn_to_san_mainline(pgn_string)
    fen_list = []
    for move in san_mainline.split():
        board.push_san(move)
        fen_list.append(((board.fen()).split())[0])
    return fen_list

def create_table():
    conn = sqlite3.connect(DB_NAME)
    cursor = conn.cursor()
    
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS openings (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            opening_name TEXT UNIQUE,
            fen_list TEXT  -- JSON stored as TEXT
        )
    ''')
    
    conn.commit()
    conn.close()

def insert_opening(opening_name, fen_list):
    conn = sqlite3.connect(DB_NAME)
    cursor = conn.cursor()

    fen_json = json.dumps(fen_list)

    cursor.execute('''
        INSERT INTO openings (opening_name, fen_array) 
        VALUES (?, ?) 
        ON CONFLICT(opening_name) DO UPDATE SET fen_array= ?
    ''', (opening_name, fen_json, fen_json))

    conn.commit()
    conn.close()

def get_all_openings():
    conn = sqlite3.connect(DB_NAME)
    cursor = conn.cursor()

    cursor.execute("SELECT opening_name, fen_array FROM openings")
    openings = cursor.fetchall()

    conn.close()

    for name, fen_json in openings:
        fen_list = json.loads(fen_json)
        print(f"Opening: {name}")
        for fen in fen_list:
            print(f"  FEN: {fen}")
        print()
        
def delete_opening_by_name(opening_name):
    conn = sqlite3.connect(DB_NAME)
    cursor = conn.cursor()

    cursor.execute('''
        DELETE FROM openings WHERE opening_name = ?
    ''', (opening_name,))

    if cursor.rowcount == 0:
        print(f"No opening found with the name '{opening_name}'.")
    else:
        print(f"Opening '{opening_name}' deleted successfully.")

    conn.commit()
    conn.close()

if __name__ == "__main__":
    create_table()
    
    pgn_string = """1. e4 c6 2. d4 d5 3. Nc3 dxe4 4. Nxe4 Bf5 5. Ng3 Bg6 6. h4 h6 7. Nf3 Nd7 8. h5
    Bh7 9. Bd3 Bxd3 10. Qxd3 Qc7 11. Bd2 e6 12. O-O-O Ngf6 13. Ne4 O-O-O 14. g3 Nxe4
    15. Qxe4 Nf6 16. Qe2 Bd6 17. c4 c5"""

    fen_list = pgn_to_fen_list(pgn_string)
    print(fen_list)
    insert_opening("caro_spassky_qc7", fen_list)
    print("Stored Openings:")
    get_all_openings()
