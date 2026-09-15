const express = require('express');
const mysql = require('mysql2');
const cors = require('cors');
const crypto = require('crypto');
const app = express();

// Cấu hình Middleware
app.use(cors());
app.use(express.json()); // Cho phép server đọc dữ liệu JSON từ Android gửi lên

// Thiết lập kết nối MySQL
const db = mysql.createConnection({
    host: 'localhost',
    user: 'root',      // Tài khoản mặc định của MySQL
    password: '123456',      // Mật khẩu mặc định (nếu bạn có cài mật khẩu ở Workbench thì điền vào đây)
    database: 'txngnx_db'
});

// Kiểm tra kết nối
db.connect((err) => {
    if (err) {
        console.error('LỖI KẾT NỐI MYSQL:', err.message);
        return;
    }
    console.log('✅ Kết nối Cơ sở dữ liệu MySQL (txngnx_db) thành công!');
});

// ==========================================
// API 1: Lấy danh sách toàn bộ Lô hàng
// ==========================================
app.get('/api/batches', (req, res) => {
    // Lệnh SQL lấy dữ liệu, sắp xếp cái mới nhất lên đầu
    const sql = "SELECT * FROM batches ORDER BY created_at DESC";
    
    db.query(sql, (err, results) => {
        if (err) {
            console.error("Lỗi lấy dữ liệu:", err);
            return res.status(500).json({ error: "Lỗi Server" });
        }
        res.json(results); // Trả về danh sách dưới dạng JSON cho Android
    });
});

// ==========================================
// API 2: Lấy thông tin chi tiết 1 Lô hàng + Danh sách Nhật ký (Cho Web index.html & Android)
// ==========================================
app.get('/api/batches/:id', (req, res) => {
    const batchId = req.params.id;

    // 1. Lấy thông tin lô hàng
    const sqlBatch = "SELECT * FROM batches WHERE id = ?";
    db.query(sqlBatch, [batchId], (err, batchResults) => {
        if (err) {
            console.error("Lỗi lấy chi tiết lô hàng:", err);
            return res.status(500).json({ error: "Lỗi Server" });
        }
        if (batchResults.length === 0) {
            return res.status(404).json({ error: "Không tìm thấy lô hàng" });
        }

        const batch = batchResults[0];

        // 2. Lấy danh sách nhật ký thuộc lô hàng này
        const sqlLogs = "SELECT * FROM logs WHERE batch_id = ? ORDER BY id ASC";
        db.query(sqlLogs, [batchId], (err, logResults) => {
            if (err) {
                console.error("Lỗi lấy nhật ký:", err);
                return res.status(500).json({ error: "Lỗi Server" });
            }

            res.json({
                batch: batch,
                logs: logResults
            });
        });
    });
});

// ==========================================
// API 3: Thêm một Lô hàng mới (Từ Popup 1)
// ==========================================
app.post('/api/batches', (req, res) => {
    // Lấy dữ liệu do Android gửi lên
    const { batch_name, product_type, start_date, location } = req.body;
    const status = "Mới khởi tạo"; // Trạng thái mặc định

    // Lệnh SQL chèn dữ liệu vào bảng batches
    const sql = "INSERT INTO batches (batch_name, product_type, start_date, location, status) VALUES (?, ?, ?, ?, ?)";
    
    db.query(sql, [batch_name, product_type, start_date, location, status], (err, result) => {
        if (err) {
            console.error("Lỗi thêm lô hàng:", err);
            return res.status(500).json({ error: "Lỗi Server" });
        }
        res.json({ 
            message: "Tạo lô hàng thành công!", 
            id: result.insertId // Trả về ID của lô hàng vừa tạo
        });
    });
});

// ==========================================
// API 4: Thêm Nhật ký chăm sóc (Có bảo mật Hash)
// ==========================================
app.post('/api/logs', (req, res) => {
    const { batch_id, action_details, log_time } = req.body;

    // 1. Tìm mã băm của nhật ký gần nhất thuộc lô hàng này
    const findLastHashSql = "SELECT record_hash FROM logs WHERE batch_id = ? ORDER BY id DESC LIMIT 1";
    
    db.query(findLastHashSql, [batch_id], (err, results) => {
        if (err) {
            console.error("Lỗi truy vấn:", err);
            return res.status(500).json({ error: "Lỗi Server" });
        }

        // 2. Xác định chuỗi liên kết
        let previous_hash = "GENESIS_NODE_N56"; // Mặc định nếu đây là nhật ký đầu tiên của lô hàng
        if (results.length > 0) {
            previous_hash = results[0].record_hash; // Lấy mã băm của nhật ký cũ nối vào
        }

        // 3. Thực hiện thuật toán Băm (SHA-256)
        // Gom tất cả dữ liệu lại thành một chuỗi duy nhất để băm
        const dataToHash = batch_id + action_details + log_time + previous_hash;
        const new_record_hash = crypto.createHash('sha256').update(dataToHash).digest('hex');

        // 4. Lưu dữ liệu cùng mã băm mới vào Database
        const insertSql = "INSERT INTO logs (batch_id, action_details, log_time, record_hash) VALUES (?, ?, ?, ?)";
        
        db.query(insertSql, [batch_id, action_details, log_time, new_record_hash], (err, result) => {
            if (err) {
                console.error("Lỗi thêm nhật ký:", err);
                return res.status(500).json({ error: "Lỗi Server" });
            }
            res.json({ 
                message: "Thêm nhật ký bảo mật thành công!",
                hash_created: new_record_hash 
            });
        });
    });
});

// ==========================================
// API 5: Lấy danh sách Nhật ký của một Lô hàng (Dành cho Android NhatKyActivity)
// ==========================================
app.get('/api/logs/:batch_id', (req, res) => {
    const batch_id = req.params.batch_id;
    const sql = "SELECT * FROM logs WHERE batch_id = ? ORDER BY id DESC";

    db.query(sql, [batch_id], (err, results) => {
        if (err) {
            console.error("Lỗi lấy nhật ký:", err);
            return res.status(500).json({ error: "Lỗi Server" });
        }
        res.json(results);
    });
});

// Hỗ trợ thêm endpoint dạng Query Param: GET /api/logs?batch_id=1
app.get('/api/logs', (req, res) => {
    const batch_id = req.query.batch_id;
    let sql = "SELECT * FROM logs ORDER BY id DESC";
    let params = [];

    if (batch_id) {
        sql = "SELECT * FROM logs WHERE batch_id = ? ORDER BY id DESC";
        params = [batch_id];
    }

    db.query(sql, params, (err, results) => {
        if (err) {
            console.error("Lỗi lấy danh sách nhật ký:", err);
            return res.status(500).json({ error: "Lỗi Server" });
        }
        res.json(results);
    });
});

// ==========================================
// API 6: Xuất chuồng lô hàng (Chốt Merkle Root Hash & Cập nhật trạng thái XUAT_CHUONG)
// ==========================================
app.post('/api/batches/:id/finalize', (req, res) => {
    const batchId = req.params.id;
    const { root_hash } = req.body;
    const status = "XUAT_CHUONG";

    const sql = "UPDATE batches SET status = ?, root_hash = ? WHERE id = ?";
    db.query(sql, [status, root_hash, batchId], (err, result) => {
        if (err) {
            console.error("Lỗi xuất chuồng:", err);
            return res.status(500).json({ error: "Lỗi Server khi xuất chuồng" });
        }
        res.json({
            message: "Xuất chuồng lô hàng thành công!",
            status: status,
            root_hash: root_hash
        });
    });
});

const PORT = 3000;
app.listen(PORT, '0.0.0.0', () => {
    console.log(`🚀 Server đang chạy tại: http://localhost:${PORT}`);
});
